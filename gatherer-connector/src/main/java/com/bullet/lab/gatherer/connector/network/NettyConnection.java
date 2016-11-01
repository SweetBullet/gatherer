package com.bullet.lab.gatherer.connector.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.IOException;

/**
 * Created by pudongxu on 16/11/1.
 */
public class NettyConnection implements Connection {

    private final Channel channel;

    public NettyConnection(Channel channel) {
        this.channel=channel;
    }


    @Override
    public void init() {

    }

    @Override
    public ChannelFuture write() {
        return channel.writeAndFlush(new Object());
    }


    @Override
    public void close() throws IOException {

    }
}
