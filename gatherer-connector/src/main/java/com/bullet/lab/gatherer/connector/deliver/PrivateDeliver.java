package com.bullet.lab.gatherer.connector.deliver;

import com.bullet.lab.gatherer.connector.network.codec.GDecoder;
import com.bullet.lab.gatherer.connector.network.codec.GEncoder;
import com.bullet.lab.gatherer.connector.network.server.PrivateServer;
import com.bullet.lab.gatherer.connector.network.server.Server;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pudongxu on 16/11/1.
 */
public class PrivateDeliver implements Deliver<ChannelHandler> {

    private final static Logger logger = LoggerFactory.getLogger(PrivateDeliver.class);

    private final Server server;

    private Channel channel;

    public PrivateDeliver() {
        server = new PrivateServer();
    }

    @Override
    public Channel start() {
        channel = server.start();
        return channel;
    }

    @Override
    public void bind(int port, ChannelHandler channelHandler) {
        server.bind(port, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast("IdleStateHandler", new IdleStateHandler(120, 120, 120));
                p.addLast("lengthDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, Integer.BYTES, -Integer.BYTES, 0));
                p.addLast("gatherDecoder",new GDecoder());
                p.addLast("gatherEncoder",new GEncoder());
                p.addLast(channelHandler);
            }
        });
        logger.debug("deliver bind port {}",port);
    }


    @Override
    public void close() throws IOException {
        logger.debug("deliver start to close");
        server.close();
        if (channel!=null)
            channel.close();
    }
}
