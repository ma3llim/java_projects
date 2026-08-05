package org.example.repositories;


import org.example.entities.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository  extends MongoRepository<Room, String> {
    // get room using roomId

    Optional<Room> findRoomByRoomId(String roomId);
}
