package transmiter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.NettyServer;
import server.Server;

import java.io.IOException;

/**
 * Created by pudongxu on 16/10/26.
 */
public class DefaultTransmiter implements Transmiter<ChannelHandler> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Server server;

    private Channel channel;

    public Channel start() {
        channel = server.start();
        return channel;
    }

    public void bind(int port, final ChannelHandler channelHandler) {
        server = new NettyServer();
        server.bind(port, new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p= ch.pipeline();
                p.addLast("httpDecoder", new HttpRequestDecoder());
//                p.addLast("idleStateHandler",new IdleStateHandler(20,0,0));
                p.addLast("httpEncoder",new HttpResponseEncoder());
                p.addLast("httpAggregator",new HttpObjectAggregator(1024*64));
                p.addLast(channelHandler);
            }
        });
    }

    public void close() throws IOException {
        logger.debug("channel close");
        if (channel != null) {
            channel.close();
        }
    }
}
