package com.bullet.lab.gatherer.core.dispatcher;

import com.bullet.lab.gatherer.core.BoundedExecutor;
import com.bullet.lab.gatherer.core.handler.Handler;
import com.bullet.lab.gatherer.core.handler.HandlerFactory;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Created by pudongxu on 16/11/9.
 */
public class DispatcherInitializer implements Closeable {

    private final static Logger logger = LoggerFactory.getLogger(DispatcherInitializer.class);

    private final static int DEFAULT_WORKER_THREAD_NUM = 16;

    private Map<String, Dispatcher> dispatcherMap = new HashMap<>();
    @Setter
    private String types;
    @Setter
    private HandlerFactory factory;

    public void init() {
        Stream.of(types.split(","))
                .map(StringUtils::trim)
                .forEach(type->{
                    Handler handler = factory.getHandler(type);
                    SingleDispatcher dispatcher = initDispatcher(type,handler);
                    dispatcherMap.put(type, dispatcher);
                });
    }

    private SingleDispatcher initDispatcher(String type, Handler handler) {
        Executor executor = new ThreadPoolExecutor(DEFAULT_WORKER_THREAD_NUM, DEFAULT_WORKER_THREAD_NUM, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new BasicThreadFactory.Builder().namingPattern(type+"-executor-%d").build());
        SingleDispatcher dispatcher = new SingleDispatcher(new BoundedExecutor(executor, DEFAULT_WORKER_THREAD_NUM), handler);
        ThreadFactory factory = new BasicThreadFactory.Builder().namingPattern(type+"-Dispatcher-%d").build();
        factory.newThread(dispatcher::dispatch).start();
        return dispatcher;
    }

    @Override
    public void close() throws IOException {
        dispatcherMap.forEach((type,dispatcher)->{
            try {
                dispatcher.close();
            } catch (Exception e) {
                logger.error("{} dispatcher close error:", type, e);
            }
        });
    }
}
