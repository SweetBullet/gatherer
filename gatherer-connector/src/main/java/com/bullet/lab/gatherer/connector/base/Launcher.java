package com.bullet.lab.gatherer.connector.base;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/11/1.
 */
public interface Launcher extends Closeable {
    void init();
}
