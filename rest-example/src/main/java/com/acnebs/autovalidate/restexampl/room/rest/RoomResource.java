package com.acnebs.autovalidate.restexampl.room.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.*;
import lombok.experimental.*;

import java.io.IOException;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class RoomResource {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    String name;
    String number;
    Integer feelGoodCapacity;
    Integer maximumCapacity;

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RoomResource fromJson(final String json) {
        try {
            return objectMapper.readValue(json, RoomResource.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<RoomResource> listFromJson(final String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<RoomResource>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
