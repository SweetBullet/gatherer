package com.bullet.lab.gatherer.connector.network.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by pudongxu on 16/10/26.
 */
public class HttpServer extends NettyServer {


    @Override
    public void bind(int port, ChannelInitializer<SocketChannel> handler) {
        initBootstrap();
        super.bind(port,handler);
    }


    @Override
    public void initBootstrap() {
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }
}
