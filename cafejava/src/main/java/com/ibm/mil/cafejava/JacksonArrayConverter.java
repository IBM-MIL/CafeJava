package com.ibm.mil.cafejava;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

public class JacksonArrayConverter<T extends List> extends JacksonConverter<T> {
    private final TypeReference<T> reference;

    public JacksonArrayConverter(TypeReference<T> reference) {
        this.reference = reference;
    }

    @Override
    T convert(String json) throws IOException {
        return new ObjectMapper().readValue(json, reference);
    }
}
