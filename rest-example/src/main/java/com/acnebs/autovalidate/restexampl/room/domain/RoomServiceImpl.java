package com.acnebs.autovalidate.restexampl.room.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Class RoomServiceStubImpl.
 * <p>
 * Created by acj on 17.07.19
 *
 * @author acj
 */
@Service
class RoomServiceImpl implements RoomService {
    private final RoomRepository repository;

    RoomServiceImpl(@Autowired final RoomRepository repository) {
        this.repository = repository;
    }

    @Override
    public void getAll(final Consumer<Room> callback) {
        repository.getAll(callback::accept);
    }

    @Override
    public Room getByNumber(final String number) {
        return repository.getByNumber(number);
    }

    @Override
    public void save(final Room room) {
        repository.save(room);
    }
}
