package com.bullet.lab.gatherer.connector.base;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import lombok.val;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Created by pudongxu on 16/11/1.
 */
public class GsonSerialization implements Serialization {

    private static final Charset DEFAULT_CHARSET= StandardCharsets.UTF_8;

    private final Gson gson;

    public GsonSerialization() {
        this(FieldNamingPolicy.IDENTITY);
    }

    public GsonSerialization(FieldNamingPolicy fieldNamingPolicy) {
        val builder = new GsonBuilder().setFieldNamingPolicy(fieldNamingPolicy).registerTypeAdapter(Instant.class,
                InstantCodec.instance());
        gson = builder.create();
    }

    @Override
    public byte[] serialize(Object obj) {
        return gson.toJson(obj).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public void serialize(Object obj, OutputStream outputStream) {
        val writer = new JsonWriter(new OutputStreamWriter(outputStream, DEFAULT_CHARSET));
        gson.toJson(obj, obj.getClass(), writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] in, Class<T> type) {
        return gson.fromJson(new String(in,DEFAULT_CHARSET), type);
    }

    @Override
    public <T> T deserialize(byte[] in, Type type) {
        return gson.fromJson(new String(in, DEFAULT_CHARSET), type);
    }

    @Override
    public <T> T deserialize(InputStream input, Type type) {
        return gson.fromJson(new InputStreamReader(input, DEFAULT_CHARSET), type);
    }

    @Override
    public <T> T deserialize(InputStream input, Class<T> type) {
        return gson.fromJson(new InputStreamReader(input, DEFAULT_CHARSET), type);
    }

    @Override
    public String serialize2String(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T deserialize2Object(String str, Type type) {
        return gson.fromJson(str, type);
    }

    @Override
    public <T> T deserialize2Object(String str, Class<T> type) {
        return gson.fromJson(str, type);
    }

    public static class InstantCodec implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        public static InstantCodec instance() {
            return instance;
        }

        private static final InstantCodec instance = new InstantCodec();

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochMilli(json.getAsLong());
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toEpochMilli());
        }

    }

}
