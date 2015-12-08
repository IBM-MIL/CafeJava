package com.ibm.mil.cafejava;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JacksonObjectConverter<T> extends JacksonConverter<T> {
    private final Class<T> clazz;

    public JacksonObjectConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    T convert(String json) throws IOException {
        return new ObjectMapper().readValue(json, clazz);
    }
}
