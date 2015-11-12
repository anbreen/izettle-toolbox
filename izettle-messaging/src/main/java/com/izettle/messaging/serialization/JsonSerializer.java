package com.izettle.messaging.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public class JsonSerializer {
    private static final ObjectMapper JSON_MAPPER = createInstance();

    private static ObjectMapper createInstance() {
        ObjectMapper result = new ObjectMapper();
        result.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        result.registerModule(new JSR310Module());
        return result;
    }

    public static ObjectMapper getInstance() {
        return JSON_MAPPER;
    }
}
