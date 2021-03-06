package com.bullet.lab.gatherer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by pudongxu on 16/11/7.
 */
public class BoundedExecutor implements Executor, AutoCloseable {

    private final static Logger logger = LoggerFactory.getLogger(BoundedExecutor.class);
    private final ExecutorService executor;
    private final Semaphore semaphore;

    public BoundedExecutor(ExecutorService executor, int bound) {
        this.executor = executor;
        this.semaphore = new Semaphore(bound);
    }


    public void execute(final Runnable command) {
        semaphore.acquireUninterruptibly();
        try {
            executor.execute(() -> {
                        try {
                            command.run();
                        } catch (Exception e) {
                            logger.error("executing task error!", e);
                            throw e;
                        } finally {
                            semaphore.release();
                        }
                    }
            );
        } catch (RejectedExecutionException e) {
            semaphore.release();
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        boolean isShutDown = this.executor.awaitTermination(60, TimeUnit.SECONDS);
        if (!isShutDown) {
            logger.warn("waiting executor shutdown timeout!");
            executor.shutdownNow();
        }
    }
}
