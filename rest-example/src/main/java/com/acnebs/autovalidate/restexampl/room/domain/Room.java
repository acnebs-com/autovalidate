package com.acnebs.autovalidate.restexampl.room.domain;

import com.acnebs.autovalidate.AutoValidate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import io.swagger.annotations.*;
import lombok.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;

@Getter
@Builder(toBuilder=true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@AutoValidate
@ApiModel
public class Room {

    @NotNull
    @Size(min = 1, max = 8)
    @ApiModelProperty(value = "The room number. Used as a global ID.")
    String number;

    @Size(min = 1, max = 256)
    String name;

    @NotNull
    @Min(0)
    @Max(100)
    Integer feelGoodCapacity;

    @NotNull
    @Min(0)
    @Max(100)
    Integer maximumCapacity;


    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Room fromJson(final String json) {
        try {
            return objectMapper.readValue(json, Room.class);
        } catch (IOException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw (ConstraintViolationException)e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    public static List<Room> listFromJson(final String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Room>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
