package com.bullet.lab.gatherer.connector.event;

import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.channel.Channel;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface EventContext {

    Channel getChannel();

    MedicalData getData();
}
