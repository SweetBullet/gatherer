package com.bullet.lab.gatherer.connector.network.codec;

import com.bullet.lab.gatherer.connector.network.frame.GFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by pudongxu on 16/11/1.
 */
public class GEncoder extends MessageToMessageEncoder<GFrame> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, GFrame gFrame, List<Object> list) throws Exception {

    }
}
