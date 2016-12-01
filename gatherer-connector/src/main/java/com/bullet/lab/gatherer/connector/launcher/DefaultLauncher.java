package com.bullet.lab.gatherer.connector.launcher;

import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.event.dispatcher.DispatcherExecutor;
import com.bullet.lab.gatherer.connector.handler.HttpHandler;
import com.bullet.lab.gatherer.connector.manager.Manager;
import io.netty.channel.ChannelHandler;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bullet.lab.gatherer.connector.deliver.Deliver;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.Closeable;
import java.io.IOException;

/**
 * Created by pudongxu on 16/10/27.
 */
public class DefaultLauncher implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLauncher.class);

    private Dispatcher dispatcher;
    @Setter
    private int port;
    @Setter
    private Deliver<ChannelHandler> deliver;
    @Setter
    private Manager manager;

    public void init() {
        logger.debug("init dispatcher");
        dispatcher = new DispatcherExecutor();
        dispatcher.register(EventType.connecting, manager::processConnect);
        dispatcher.register(EventType.receive, manager::processDefault);
        dispatcher.register(EventType.disconnected, manager::processDisconnect);
        this.initDelivery();
    }


    private void initDelivery() {
        logger.debug("init delivery");
        deliver.bind(port, new HttpHandler(dispatcher));
        deliver.start();
    }

    @Override
    public void close() throws IOException {
        deliver.close();
    }


    public static void main(String[] args) {
        try {
            logger.debug("main method");
            final AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/application*.xml");
            context.start();
            Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread") {
                @Override
                public void run() {
                    System.out.println("closing connector");
                    context.close();
                }
            });
        } catch (Exception e) {
            logger.error("start fail:", e);
            System.exit(-1);
        }

    }
}
