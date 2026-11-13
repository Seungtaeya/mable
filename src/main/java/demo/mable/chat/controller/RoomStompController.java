package demo.mable.chat.controller;

import demo.mable.chat.dto.ChatMessageResponse;
import demo.mable.chat.dto.MessageRequest;
import demo.mable.chat.service.MessageService;
import demo.mable.room.dto.*;
import demo.mable.room.service.GameRoomService;
import demo.mable.room.service.RoomPlayerService;
import demo.mable.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RoomStompController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameRoomService gameRoomService;
    private final MessageService messageService;
    private final RoomPlayerService roomPlayerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void LeaveRoomEvent(LeaveRoom leaveRoom) {
        GameRoomResponse gameRoom = gameRoomService.getGameRoom(leaveRoom.roomId());
        gameRoomService.closeRoomIfEmpty(leaveRoom.roomId());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void GameRoomChangeHostEvent(GameRoomChangeHostEvent event) {
        simpMessagingTemplate.convertAndSendToUser(event.userName(),"/queue/room/" + event.roomId() + "/actions", new HostAuthorityResponse(event.roomId(),true));
    }

    @MessageMapping("/rooms/{roomId}")
    public void roomChat(@DestinationVariable Long roomId, @Payload MessageRequest messageRequest, Principal principal) {
        System.out.printf("roomChat");
        ChatMessageResponse chatMessageResponse = messageService.saveGameRoomMessage(roomId, messageRequest, principal.getName());
        simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId + ".chatMessage", chatMessageResponse);
    }

    @MessageMapping("/rooms/{roomId}/action")
    public void gameAction(@DestinationVariable Long roomId, Principal principal) {
        GameRoomResponse gameRoom = gameRoomService.getGameRoom(roomId);

        if (gameRoom.getHostName().equals(principal.getName())) {
            GameStartedResponse gameStartedResponse = roomPlayerService.startAction(roomId, principal.getName());
            simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/gameStart", gameStartedResponse);
        } else {
        RoomActionResponse roomActionResponse = roomPlayerService.gameAction(roomId, principal.getName());
        simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/action", roomActionResponse);
        }
    }
}
