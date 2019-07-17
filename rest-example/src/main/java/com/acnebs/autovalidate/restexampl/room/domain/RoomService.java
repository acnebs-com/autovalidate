package com.acnebs.autovalidate.restexampl.room.domain;


import org.springframework.lang.*;

import java.util.function.Consumer;

public interface RoomService {

    void getAll(@NonNull Consumer<Room> callback);

    @Nullable
    Room getByNumber(String number);

    void save(Room room);
}
