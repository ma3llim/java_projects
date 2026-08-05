package org.example.services;

import org.example.entities.Message;
import org.example.entities.Room;
import org.example.repositories.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public ResponseEntity<?> createRoom(String roomId) {
        if (roomRepository.findByRoomId(roomId).isPresent()){
            return ResponseEntity.badRequest().body("Room Id is Already There!!");
        }

        Room room = Room.builder().roomId(roomId).build();
        Room newRoom = roomRepository.save(room);

        return ResponseEntity.ok(newRoom);
    }

    public ResponseEntity<?> joinRoom(String roomId) {
        Optional<Room> room = roomRepository.findByRoomId(roomId);
        if (room.isEmpty()){
            return ResponseEntity.badRequest().body("Room Not Found!!");
        }

        return ResponseEntity.ok(room);
    }

    public ResponseEntity<List<Message>> getMessages(String roomId){
        Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Not Found"));
         return ResponseEntity.ok(room.getMessages());
    }
}
