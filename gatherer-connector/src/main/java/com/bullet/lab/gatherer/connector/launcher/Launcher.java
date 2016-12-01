package com.bullet.lab.gatherer.connector.launcher;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/11/1.
 */
public interface Launcher extends Closeable {
    void init();
}
