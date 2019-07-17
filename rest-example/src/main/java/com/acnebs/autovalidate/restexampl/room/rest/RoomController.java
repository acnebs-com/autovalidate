package com.acnebs.autovalidate.restexampl.room.rest;


import com.acnebs.autovalidate.restexampl.common.rest.ControllerBase;
import com.acnebs.autovalidate.restexampl.room.domain.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/rooms")
@Api
class RoomController extends ControllerBase {

    private final RoomService roomService;

    RoomController(@Autowired final RoomService roomService) {
        super(
                (RoomHalResource::new),
                (resourceSupport -> {
                    RoomHalResource<RoomResource> hres = (RoomHalResource)resourceSupport;
                    return hres.getPayload().getNumber();
                }),
                RoomResource.class
        );

        this.roomService = roomService;
    }

    @GetMapping("")
    public List<RoomHalResource> getAll() {
        final List<RoomHalResource> out = new ArrayList<>();
        roomService.getAll(room -> toHalResource(room).ifPresent(res -> out.add((RoomHalResource) res)));
        return out;
    }

    @PutMapping("/{number}")
    @ApiOperation(value = "Save the sent room.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataTypeClass = Room.class, paramType = "body")
    })

    public HttpEntity<RoomHalResource> putRoom(
            @PathVariable final String number,
            @RequestBody  final String json
    ) {
        try {
            final Room room = Room.fromJson(json);
            /*final Room room = Room.builder()
                    .number(number)
                    .name(roomResource.getName())
                    .feelGoodCapacity(roomResource.getFeelGoodCapacity())
                    .maximumCapacity(roomResource.getMaximumCapacity())
                    .build();*/
            roomService.save(room);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ConstraintViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{number}")
    public HttpEntity<RoomHalResource> getByNumber(
            @PathVariable final String number
    ) {
        return toHalResource(roomService.getByNumber(number))
                .map(hres0 -> (RoomHalResource)hres0)
                .map(hres -> new ResponseEntity<>(hres, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    protected HttpEntity<RoomHalResource> getLinkTarget(final String number) {
        return methodOn(RoomController.class).getByNumber(number);
    }

}
