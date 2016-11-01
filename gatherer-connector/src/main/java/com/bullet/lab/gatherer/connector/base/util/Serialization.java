package com.bullet.lab.gatherer.connector.base.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by pudongxu on 16/11/1.
 */

public interface Serialization {
    byte[] serialize(Object var1);

    default void serialize(Object obj, OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }

    <T> T deserialize(byte[] var1, Class<T> var2);

    default <T> T deserialize(byte[] in, Type type) {
        if (type instanceof Class) {
            return (T) this.deserialize(in, (Class) type);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    default <T> T deserialize(InputStream input, Type type) {
        throw new UnsupportedOperationException();
    }

    default <T> T deserialize(InputStream input, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    default String serialize2String(Object obj) {
        throw new UnsupportedOperationException();
    }

    default <T> T deserialize2Object(String str, Type type) {
        if (type instanceof Class) {
            return (T) this.deserialize2Object(str, (Class) type);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    default <T> T deserialize2Object(String str, Class<T> type) {
        throw new UnsupportedOperationException();
    }
}

