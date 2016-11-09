package com.bullet.lab.gatherer.core.dispatcher;

import java.io.Closeable;
import java.util.function.Consumer;

/**
 * Created by pudongxu on 16/11/9.
 */
public interface Dispatcher<T> extends Consumer<T>,Closeable{
    void dispatch();
}
