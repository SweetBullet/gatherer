package com.bullet.lab.gatherer.connector.handler;

import com.bullet.lab.gatherer.connector.base.util.GsonSerialization;
import com.bullet.lab.gatherer.connector.base.util.Serialization;
import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.network.command.Command;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pudongxu on 16/11/1.
 */
public class WsHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final static Logger logger = LoggerFactory.getLogger(WsHandler.class);

    private final Dispatcher dispatcher;

    private final Serialization serialization = new GsonSerialization();

    public WsHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {


        dispatcher.dispatch(EventType.receive, new EventContext() {
            @Override
            public Channel getChannel() {
                return ctx.channel();
            }

            @Override
            public MedicalData getData() {
                String data=frame.text();
                return serialization.deserialize2Object(data, MedicalData.class);
            }
        });
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("read event timeout!");
    }
}
