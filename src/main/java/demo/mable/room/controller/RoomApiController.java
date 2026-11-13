package demo.mable.room.controller;

import demo.mable.global.security.CustomUserDetails;
import demo.mable.room.dto.*;
import demo.mable.room.service.GameRoomService;
import demo.mable.room.service.RoomPlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomApiController {

    private final GameRoomService gameRoomService;
    private final RoomPlayerService roomPlayerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/rooms")
    public ResponseEntity<List<GameRoomResponse>> getRooms() {
        List<GameRoomResponse> rooms = gameRoomService.getRooms();
        return ResponseEntity.ok().body(rooms);
    }

    @GetMapping("/rooms/{roomId}/hostId")
    public ResponseEntity<Boolean> getGameRoomHostId(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isHost = gameRoomService.isUserHost(roomId, userDetails.getUserId());

        return ResponseEntity.ok(isHost);
    }

    @PostMapping("/rooms")
    public ResponseEntity<Long> registerRoom(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody GameRoomRequest request, BindingResult bindingResult) {
        System.out.println("/api/rooms in");

        Long gameRoomId = gameRoomService.createGameRoom(userDetails.getUserId(), request);
        return ResponseEntity.ok().body(gameRoomId);
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<Long> joinGameRoom(@PathVariable(name = "roomId")Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("joinGameRoom Call");
        JoinRoomResponse joinRoomResponse = gameRoomService.joinRoom(roomId, userDetails.getUserId());
        List<GameWaitingRoomPlayerResponse> roomPlayers = roomPlayerService.getRoomPlayers(roomId);
        simpMessagingTemplate.convertAndSend("/topic/rooms/"+roomId, roomPlayers);
        return ResponseEntity.ok().body(roomId);
    }

    @GetMapping("/rooms/{roomId}/players")
    public ResponseEntity<List<GameWaitingRoomPlayerResponse>> getPlayers(@PathVariable Long roomId) {
        List<GameWaitingRoomPlayerResponse> roomPlayers = roomPlayerService.getRoomPlayers(roomId);

        return ResponseEntity.ok().body(roomPlayers);
    }

    @DeleteMapping("/rooms/{roomId}/players/me")
    public ResponseEntity<Void> leaveRoom(@PathVariable(name = "roomId")Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        int playerCount = roomPlayerService.leaveRoom(roomId, userDetails.getUserId());

        if(playerCount != 0) {
            List<GameWaitingRoomPlayerResponse> roomPlayers = roomPlayerService.getRoomPlayers(roomId);
            simpMessagingTemplate.convertAndSend("/topic/rooms/" +roomId, roomPlayers );
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/rooms")
    public ResponseEntity<Void> clearAllRooms() {
        gameRoomService.clearAllRoomsForTest();
        return ResponseEntity.ok().build();
    }
}
