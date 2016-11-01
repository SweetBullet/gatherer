package com.bullet.lab.gatherer.connector.pojo;

import lombok.Getter;

/**
 * Created by pudongxu on 16/11/1.
 */
public class Address {

    @Getter
    private final String host;
    @Getter
    private final int port;

    public Address(String host, String port) {
        this.host=host;
        this.port = Integer.valueOf(port);
    }

    public Address(String host, int port) {
        this.host=host;
        this.port = port;
    }
}
