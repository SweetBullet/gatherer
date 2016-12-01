package com.bullet.lab.gatherer.connector.manager;

import com.bullet.lab.gatherer.connector.base.Constants;
import com.bullet.lab.gatherer.connector.base.RequestType;
import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by pudongxu on 16/12/1.
 */
public class HttpManager implements Manager {

    private final static Logger logger = LoggerFactory.getLogger(HttpManager.class);

    @Override
    public void processConnect(EventContext ec) {
        logger.debug("process connect event");
        Channel channel = ec.channel();
        channel.attr(Constants.IS_VALID_KEY).set(false);
    }

    @Override
    public void processDefault(EventContext ec) {
        RequestType reqType = ec.reqType();
        switch (reqType) {
            case auth:
                break;
            case msg:
                MedicalData data = new MedicalData(ec.params().get("name"), ec.params().get("phone"));
                logger.debug("receive data:{}", data);
                //// TODO: 16/12/1 save
                try {
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.OK, Unpooled.wrappedBuffer("success!!!".getBytes("UTF-8")));
                    response.headers().set(CONTENT_TYPE, "text/plain");
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    ec.channel().writeAndFlush(response);
                    logger.debug("write response!");
                } catch (Exception e) {
                    logger.error("error in responding");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void processDisconnect(EventContext ctx) {
        logger.debug("process disconnect event");
        //// TODO: 16/12/1 close/remove
    }
}
