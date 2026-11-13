package demo.mable.room.controller;

import demo.mable.global.security.CustomUserDetails;
import demo.mable.room.dto.GameWaitingRoomPlayerResponse;
import demo.mable.room.dto.GameRoomResponse;
import demo.mable.room.service.GameRoomService;
import demo.mable.room.service.RoomPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final GameRoomService gameRoomService;
    private final RoomPlayerService roomPlayerService;

    @GetMapping("/lobby")
    public String robbyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

//        List<GameRoomResponse> rooms = gameRoomService.getRooms();
//        model.addAttribute("rooms", rooms);
        model.addAttribute("username",customUserDetails.getUsername());
//        model.addAttribute("userId",customUserDetails.getUserId());

        return "room/lobby";
    }

    @GetMapping("/room")
    public String registerRoomPage() {
        return "room/createRoomForm";
    }

    @GetMapping("/rooms/{roomId}")
    public String GameWaitingRoom(@PathVariable Long roomId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GameWaitingRoomPlayerResponse> roomPlayers = roomPlayerService.getRoomPlayers(roomId);
        GameRoomResponse gameRoom = gameRoomService.getGameRoom(roomId);
        model.addAttribute("players",roomPlayers);
        model.addAttribute("gameRoom",gameRoom);
        model.addAttribute("currentUserId", userDetails.getUserId());
        return "room/GameWaitingRoom";
    }

}
