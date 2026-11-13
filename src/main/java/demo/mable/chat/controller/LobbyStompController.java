package demo.mable.chat.controller;

import demo.mable.chat.dto.ChatMessageResponse;
import demo.mable.chat.dto.MessageRequest;
import demo.mable.chat.service.MessageService;
import demo.mable.room.dto.RoomCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LobbyStompController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/lobby/chat")
    public void lobbyChat(@Payload MessageRequest messageRequest, Principal principal) {

        System.out.println("로비 챗 저장 시작");
        ChatMessageResponse chatMessageResponse = messageService.saveLobbyMessage(messageRequest, principal.getName());
        simpMessagingTemplate.convertAndSend("/topic/lobby/chat",chatMessageResponse);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void roomCreatedEvent(RoomCreatedEvent event) {

        simpMessagingTemplate.convertAndSend("/topic/rooms/create", event.room());
    }

}
