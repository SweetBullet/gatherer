package com.bullet.lab.gatherer.connector.event;

import io.netty.channel.Channel;
import com.bullet.lab.gatherer.connector.protocol.MedicalData;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface EventContext {

    Channel getChannel();

    MedicalData getData();
}
