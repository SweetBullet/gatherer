package com.bullet.lab.gatherer.connector.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pudongxu on 16/10/26.
 */
public class NettyServer implements Server<ChannelInitializer<SocketChannel>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ServerBootstrap bootstrap;

    private int port;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    public Channel start() {
        Channel channel = bootstrap.bind(port).syncUninterruptibly().channel();
        logger.debug("netty server start!");
        return channel;
    }

    public void bind(int port, ChannelInitializer<SocketChannel> handler) {
        this.port = port;
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(handler);
        logger.debug("netty server bind port {}", port);

    }

    public void close() throws IOException {
        if (boss != null)
            boss.shutdownGracefully();
        if (worker != null)
            worker.shutdownGracefully();
    }
}