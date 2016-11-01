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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pudongxu on 16/11/1.
 */
public class PrivateHandler extends SimpleChannelInboundHandler<Command> {

    private final static Logger logger = LoggerFactory.getLogger(PrivateHandler.class);

    private final Dispatcher dispatcher;

    private final Serialization serialization = new GsonSerialization();

    public PrivateHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        Command.CommandType type = command.getCommandType();
        if (type == Command.CommandType.query) {
            dispatcher.dispatch(EventType.receive, new EventContext() {
                @Override
                public Channel getChannel() {
                    return channelHandlerContext.channel();
                }

                @Override
                public MedicalData getData() {
                    String s=command.getMessage();
                    return serialization.deserialize2Object(s, MedicalData.class);
                }
            });
        }
    }
}
