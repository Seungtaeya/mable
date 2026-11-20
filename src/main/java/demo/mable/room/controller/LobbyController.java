package demo.mable.room.controller;

import demo.mable.global.security.CustomUserDetails;
import demo.mable.room.dto.GameWaitingRoomPlayerResponse;
import demo.mable.room.dto.GameRoomResponse;
import demo.mable.room.service.GameRoomService;
import demo.mable.room.service.RoomPlayerService;
import demo.mable.user.dto.UserRankingResponse;
import demo.mable.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final GameRoomService gameRoomService;
    private final RoomPlayerService roomPlayerService;
    private final UserService userService;

    @GetMapping("/lobby")
    public String robbyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        model.addAttribute("username",customUserDetails.getUsername());
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

    @GetMapping("/ranking")
    public String ranking(@RequestParam(name = "page", defaultValue = "1") int page, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<UserRankingResponse> rankings = userService.getRankings(page - 1);
        int totalCount = userService.getRankingCount();
        int totalPage = (int) ((totalCount + 9) / 10);
        int startPage = ((page - 1) / 10) * 10 + 1;
        int endPage = Math.min(startPage + 9, totalPage);

        model.addAttribute("rankings", rankings);
        model.addAttribute("username",userDetails.getUsername());
        model.addAttribute("page", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", totalPage);
        return "room/ranking";
    }

}
