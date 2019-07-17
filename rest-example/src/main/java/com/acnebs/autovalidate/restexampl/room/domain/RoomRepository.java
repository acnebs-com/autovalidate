package com.acnebs.autovalidate.restexampl.room.domain;


import org.springframework.lang.*;

import java.util.function.Consumer;

/**
 * Interface RoomRepository.
 * <p>
 * Created by acj on 17.07.19
 *
 * @author acj
 */

public interface RoomRepository {

    void getAll(@NonNull Consumer<Room> callback);

    @Nullable
    Room getByNumber(String number);

    void save(Room roomResource);
}
