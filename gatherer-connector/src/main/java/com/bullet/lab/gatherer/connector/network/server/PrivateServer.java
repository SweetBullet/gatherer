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
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        bootstrap.option(ChannelOption.SO_TIMEOUT, 1000);
    }

    @Override
    public void bind(int port, ChannelInitializer<SocketChannel> handler) {
        initBootstrap();
        super.bind(port,handler);
    }
}
