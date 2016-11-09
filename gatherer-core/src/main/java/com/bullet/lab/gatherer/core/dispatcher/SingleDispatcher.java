package com.bullet.lab.gatherer.core.dispatcher;

import com.bullet.lab.gatherer.core.BoundedExecutor;
import com.bullet.lab.gatherer.core.Message;
import com.bullet.lab.gatherer.core.handler.Handler;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by pudongxu on 16/11/9.
 */
public class SingleDispatcher implements Dispatcher<Message> {

    private final static Logger logger = LoggerFactory.getLogger(SingleDispatcher.class);

    private final BoundedExecutor executor;

    private final Handler handler;

    private final Queue<Message> taskQueue;

    private final Lock lock = new ReentrantLock();

    private final AtomicInteger taskCounter = new AtomicInteger(0);

    private final Condition fullCondition = lock.newCondition();
    private final Condition emptyCondition = lock.newCondition();
    private final Condition terminateCondition = lock.newCondition();

    private volatile Status status;

    private volatile boolean isFull;
    private volatile boolean isEmpty;

    private final static int MAX_QUEUE_SIZE = 2048;
    private final static int WAIT_PERIOD = 200;
    private final static int WAIT_THRESHOLD = 512;
    private final static int MAX_RETRY_TIME = 10;

    public SingleDispatcher(BoundedExecutor executor, Handler handler) {
        this.executor = executor;
        this.handler = handler;
        this.taskQueue = new ConcurrentLinkedQueue<>();
        this.status = Status.ready;
    }


    @Override
    public void accept(Message msg) {
        this.waitIfFull();
        this.taskQueue.add(msg);
        this.taskCounter.incrementAndGet();
        this.notifyIfEmptyWaiting();
    }

    @Override
    public void dispatch() {
        this.status = Status.running;
        int emptyCount = 0;
        while (true) {
            Message msg = taskQueue.poll();
            if (msg == null) {
                logger.debug("there is no task in the queue");
                if (status == Status.terminating) {
                    break;
                }
                emptyCount++;
            } else {
                emptyCount = 0;
                this.taskCounter.decrementAndGet();
                try {
                    this.executor.execute(() -> {
                        try {
                            handler.handle(msg);
                        } catch (Exception e) {
                            logger.error("transmit error");
                        }
                    });
                } catch (Exception e) {
                    logger.error("submit error");
                }
                this.notifyIfFullWaiting();
            }

            if (emptyCount >= WAIT_THRESHOLD) {
                this.waitWhileEmpty();
            }
        }

        lock.lock();
        try {
            this.status = Status.terminated;
            this.terminateCondition.signalAll();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }


    private void waitIfFull() {
        int waitCount = 0;
        while (taskQueue.size() >= MAX_QUEUE_SIZE) {
            lock.lock();
            try {
                this.isFull = true;
                int waitTime = (++waitCount / +1) * WAIT_PERIOD;
                waitTime = waitTime > 1000 ? 1000 : waitTime;
                logger.debug("try to wait {} milliSeconds", waitTime);
                this.fullCondition.await(waitTime, TimeUnit.MILLISECONDS);
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }
        }
    }

    private void waitWhileEmpty() {
        lock.lock();
        try {
            this.isEmpty = true;
            this.emptyCondition.await(WAIT_PERIOD, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }

    }

    private void notifyIfEmptyWaiting() {
        if (isEmpty) {
            lock.lock();
            try {
                logger.debug("try to notify the empty waiting thread");
                this.emptyCondition.signalAll();
                isEmpty = false;
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }
        }

    }

    private void notifyIfFullWaiting() {
        int spareCount = MAX_QUEUE_SIZE - taskCounter.get();
        if (isFull && spareCount >= MAX_QUEUE_SIZE / 2) {
            lock.lock();
            try {
                logger.debug("try to notify the full waiting thread");
                this.fullCondition.signalAll();
                this.isFull = false;
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }
        }

    }

    @Override
    public void close() throws IOException {
        logger.debug("try to close the Dispatcher,remaining task size is {}", taskCounter);
        lock.lock();
        try {
            if (status == Status.running) {
                this.status = Status.terminating;
                val isTimeOut = this.terminateCondition.await(WAIT_PERIOD, TimeUnit.MILLISECONDS);

                if (!isTimeOut)
                    logger.debug("close timeout");
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }


    private enum Status {
        ready, running, terminating, terminated
    }
}

