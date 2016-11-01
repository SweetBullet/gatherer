package com.bullet.lab.gatherer.connector.network.command;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by pudongxu on 16/11/1.
 */
public abstract class Command {
    public static final Charset ASCII= StandardCharsets.US_ASCII;
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final Charset DEFAULT_CHARSET=UTF8;


    @Setter
    @Getter
    private byte[] data;

    public abstract String getMessage();

    public abstract CommandType getCommandType();

    public enum CommandType {
        query(0),insert(1),delete(2),update(3);

        private int type;

        CommandType(int typeCode) {
            this.type=typeCode;
        }
    }


    public static Command newInstance(int type) {
        switch (type) {
            case 0:
                return new Query();
            default:
                return null;
        }
    }
}

