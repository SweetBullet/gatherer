package com.bullet.lab.gatherer.connector.network.command;


import java.util.Arrays;

/**
 * Created by pudongxu on 16/11/1.
 */
public class Query extends Command {

    public Query() {}

    public Query(byte[] data) {
        super.setData(data);
    }

    @Override
    public String getMessage() {

        byte[] message = Arrays.copyOfRange(getData(),8,getMessage().length());
        return new String(message,UTF8).trim();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.query;
    }
}
