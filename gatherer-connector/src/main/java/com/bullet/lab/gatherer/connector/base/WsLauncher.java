package com.bullet.lab.gatherer.connector.base;

import com.bullet.lab.gatherer.connector.deliver.WsDeliver;
import com.bullet.lab.gatherer.connector.deliver.Deliver;
import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.event.dispatcher.DispatcherExecutor;
import com.bullet.lab.gatherer.connector.handler.WsHandler;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.channel.ChannelHandler;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pudongxu on 16/11/1.
 */
public class WsLauncher implements Launcher {

    private static final Logger logger = LoggerFactory.getLogger(WsLauncher.class);
    private Dispatcher dispatcher;
    @Setter
    private int port;
    @Setter
    private Deliver<ChannelHandler> deliver;

    @Override
    public void init() {
        dispatcher = new DispatcherExecutor();
        dispatcher.register(EventType.connecting, this::processConnect);
        dispatcher.register(EventType.receive, this::processReceive);
        dispatcher.register(EventType.disconnected, this::processDisconnect);
        this.initDeliver();
    }


    private void processConnect(EventContext context) {
        logger.debug("process connect event");
        context.getChannel().writeAndFlush("connect success");
    }

    private void processReceive(EventContext context) {
        MedicalData data = context.getData();
        logger.debug("receive data:{}",data);

        //// TODO: 16/11/1 response
//        context.getChannel();
    }

    private void processDisconnect(EventContext context) {
        logger.debug("process disconnect event");
    }

    private void initDeliver() {
        deliver = new WsDeliver();
        deliver.bind(port, new WsHandler(dispatcher));
        deliver.start();
    }

    @Override
    public void close() throws IOException {
        //// TODO: 16/11/1
    }



}
