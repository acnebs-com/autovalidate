package com.acnebs.autovalidate.restexampl.room.repository;


import com.acnebs.autovalidate.restexampl.room.domain.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Consumer;

/**
 * Class RoomServiceStubImpl.
 * <p>
 * Created by acj on 17.07.19
 *
 * @author acj
 */
@Repository
public class RoomRepositoryInMemImpl implements RoomRepository {
    final Map<String, Room> rooms = new HashMap<>();

    public void addRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }

    @Override
    public void getAll(final Consumer<Room> callback) {
        rooms.values().stream().forEach(callback::accept);
    }

    @Override
    public Room getByNumber(final String number) {
        return rooms.get(number);
    }

    @Override
    public void save(final Room room) {
        rooms.put(room.getNumber(), room);
    }
}
