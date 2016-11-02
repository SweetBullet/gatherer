package com.bullet.lab.gatherer.connector.network.server;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by pudongxu on 16/11/1.
 */
public class PrivateServer extends NettyServer {

    @Override
    public void initBootstrap() {
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .childOption(ChannelOption.SO_TIMEOUT, 1000);
    }

    @Override
    public void bind(int port, ChannelInitializer<SocketChannel> handler) {
        initBootstrap();
        super.bind(port, handler);
    }
}
