package com.bullet.lab.gatherer.connector.base;

import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.event.dispatcher.DispatcherExecutor;
import com.bullet.lab.gatherer.connector.handler.DefaultHandler;
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
public class Launcher implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private Dispatcher dispatcher;

    @Setter
    private int port;
    @Setter
    private Deliver<ChannelHandler> deliver;

    public void init() {
        logger.debug("init dispatcher");
        dispatcher=new DispatcherExecutor();
        dispatcher.register(EventType.connecting,this::connect);
        dispatcher.register(EventType.receive,this::processReceive);
        dispatcher.register(EventType.disconnected,this::disconnect);
        this.initTransmiter();
    }


    private void connect(EventContext eventContext) {

    }

    private void processReceive(EventContext eventContext) {
        logger.debug("receive data:{}",eventContext.getData());
    }

    private void disconnect(EventContext eventContext) {

    }

    private void initTransmiter() {
        logger.debug("init transmiter");
        deliver.bind(port,new DefaultHandler(dispatcher));
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

//            Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread") {
//
//                @Override
//                public void run() {
//                    System.out.println("closing connector");
//                    context.close();
//                }
//            });

        } catch (Exception e) {
            logger.error("start fail:", e);
            System.exit(-1);
        }

    }
}
