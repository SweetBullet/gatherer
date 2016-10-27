package com.bullet.lab.gatherer.connector.event.dispatcher;

import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.processor.EventProcessor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by pudongxu on 16/10/26.
 */
public class DispatcherExecutor implements Dispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherExecutor.class);

    private final Dispatcher next;

    private static final int DEFAULT_THREAD_POOL_SIZE = 16;

    public DispatcherExecutor() {
        next = new EventDispatcher();
    }

    private final ExecutorService executor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE
            , 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
            , new BasicThreadFactory.Builder().namingPattern("connector-event-dispatcher-%d").build());

    @Override
    public void register(EventType eventType, EventProcessor eventProcessor) {
        next.register(eventType, eventProcessor);
    }

    @Override
    public void dispatch(EventType eventType, EventContext eventContext) {
        executor.execute(() ->
                next.dispatch(eventType, eventContext));
    }

    @Override
    public void close() throws IOException {
        next.close();
        executor.shutdown();
        try {
            boolean isTerminated = executor.awaitTermination(60, TimeUnit.SECONDS);
            if (isTerminated) {
                executor.shutdownNow();
                logger.warn(" dispatcher excutor shutdown timeout!");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            logger.warn("unexpected shutdown!");
        }
    }


    private static class EventDispatcher implements Dispatcher {

        private final Map<EventType, EventProcessor> handlers = new ConcurrentHashMap<>();


        @Override
        public void register(EventType eventType, EventProcessor eventProcessor) {
            handlers.put(eventType, eventProcessor);
            logger.info("register event handler:{}", eventType);
        }

        @Override
        public void dispatch(EventType eventType, EventContext eventContext) {
            handlers.getOrDefault(eventType, (context) ->
                    logger.warn("unexpected eventType type:{}", eventType)
            ).process(eventContext);
        }

        @Override
        public void close() throws IOException {
            //// TODO: 16/10/26
        }
    }

}
