package org.example.controller;

import org.example.Dtos.request.MessageRequestDto;
import org.example.entities.Message;
import org.example.entities.Room;
import org.example.repositories.RoomRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {
    private final RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(@DestinationVariable String roomId, @Payload MessageRequestDto requestDto){
        Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new RuntimeException("Room Not Found!!"));

        Message message = Message.builder().content(requestDto.getContent())
                .sender(requestDto.getContent())
                .build();

        room.getMessages().add(message);
        roomRepository.save(room);

        return message;
    }
}
