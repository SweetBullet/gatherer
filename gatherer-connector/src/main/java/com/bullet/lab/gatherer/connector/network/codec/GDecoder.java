package com.bullet.lab.gatherer.connector.network.codec;

import com.bullet.lab.gatherer.connector.network.command.Command;
import com.bullet.lab.gatherer.connector.network.command.Query;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by pudongxu on 16/11/1.
 */
public class GDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int type = in.readInt();
        Command command = Command.newInstance(type);
        int length = in.readInt();
        byte[] data = in.readBytes(length).array();
        command.setData(data);
        list.add(command);
    }
}
