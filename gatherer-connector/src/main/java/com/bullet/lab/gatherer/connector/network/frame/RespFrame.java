package com.bullet.lab.gatherer.connector.network.frame;




/**
 * Created by pudongxu on 16/11/1.
 */
public class RespFrame implements GFrame {

    private final byte[] data;

    private final int size;

    public RespFrame(byte[] data) {
        this.data=data;
        this.size=data.length;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getMessage() {
        return new String(data,UTF8).trim();
    }
}
