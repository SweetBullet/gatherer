package com.bullet.lab.gatherer.connector.base;

import io.netty.util.AttributeKey;

/**
 * Created by pudongxu on 16/12/1.
 */
public interface Constants {

    //-------------------netty attribute key---------------------------
    AttributeKey<Boolean> IS_VALID_KEY = AttributeKey.valueOf("isValid");
    AttributeKey<Integer> CONNECTION_ID_KEY = AttributeKey.valueOf("connectionId");
}
