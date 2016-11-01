package com.bullet.lab.gatherer.connector.network.frame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by pudongxu on 16/11/1.
 */
public interface GFrame {
    Charset UTF8 = StandardCharsets.UTF_8;
    Charset ASCII = StandardCharsets.US_ASCII;
    Charset DEFAULT_CHARSET = UTF8;

    byte[] getData();

    String getMessage();
}
