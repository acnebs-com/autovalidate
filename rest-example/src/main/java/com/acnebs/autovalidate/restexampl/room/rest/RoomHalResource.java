package com.acnebs.autovalidate.restexampl.room.rest;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;

/**
 * Class RoomHalResource.
 * <p>
 * Created by acj on 17.07.19
 *
 * @author acj
 */

class RoomHalResource<T> extends ResourceSupport {

    private final T payload;

    RoomHalResource(final T payload) {
        this.payload = payload;
    }

    @JsonUnwrapped
    T getPayload() {
        return payload;
    }
}
