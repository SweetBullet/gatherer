package com.bullet.lab.gatherer.connector.event;

import com.bullet.lab.gatherer.connector.base.RequestType;
import com.bullet.lab.gatherer.connector.manager.HttpRequestParam;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Map;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface EventContext {

    Channel channel();

    default RequestType reqType() {
        return RequestType.none;
    }

    default HttpRequestParam httpParam(){
        return null;
    }


}
