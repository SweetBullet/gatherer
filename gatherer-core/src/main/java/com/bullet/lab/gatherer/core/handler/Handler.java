package com.bullet.lab.gatherer.core.handler;

/**
 * Created by pudongxu on 16/11/9.
 */
public interface Handler<T> {
    void handle(T msg);
}
