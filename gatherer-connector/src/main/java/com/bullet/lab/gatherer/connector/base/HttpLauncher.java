package com.bullet.lab.gatherer.connector.base;

import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.event.dispatcher.DispatcherExecutor;
import com.bullet.lab.gatherer.connector.handler.HttpHandler;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import com.bullet.lab.gatherer.dao.MedicalDataDao;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bullet.lab.gatherer.connector.deliver.Deliver;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.io.Closeable;
import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by pudongxu on 16/10/27.
 */
public class HttpLauncher implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(HttpLauncher.class);

    @Resource
    private MedicalDataDao medicalDataDao;

    private Dispatcher dispatcher;

    @Setter
    private int port;
    @Setter
    private Deliver<ChannelHandler> deliver;

    public void init() {
        logger.debug("init dispatcher");
        dispatcher=new DispatcherExecutor();
        dispatcher.register(EventType.connecting,this::connect);
        dispatcher.register(EventType.receive,this::processReceive);
        dispatcher.register(EventType.disconnected,this::disconnect);
        this.initDelivery();
    }


    private void connect(EventContext eventContext) {
        logger.debug("process connect event");
    }

    private void processReceive(EventContext eventContext) {
        MedicalData medicalData=eventContext.getData();
        logger.debug("receive data:{}",medicalData);
//        MedicalDataEntity entity=new MedicalDataEntity(medicalData.getName(),medicalData.getPhone(),25);
//        medicalDataDao.insertMedicalData(entity);
//        logger.debug(medicalDataDao.getMedicalData(1).toString());

        try {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer("success!!!".getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            eventContext.getChannel().writeAndFlush(response);
            logger.debug("write response!");
        } catch (Exception e) {
            logger.error("error in responding");
        }

    }

    private void disconnect(EventContext eventContext) {
        logger.debug("process disconnect event");
    }

    private void initDelivery() {
        logger.debug("init delivery");
        deliver.bind(port,new HttpHandler(dispatcher));
        deliver.start();
    }

    @Override
    public void close() throws IOException {
        deliver.close();
    }



    public static void main(String[] args) {
        try {
            logger.debug("main method");
            final AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/application*.xml");
            context.start();
            Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread") {
                @Override
                public void run() {
                    System.out.println("closing connector");
                    context.close();
                }
            });
        } catch (Exception e) {
            logger.error("start fail:", e);
            System.exit(-1);
        }

    }
}
