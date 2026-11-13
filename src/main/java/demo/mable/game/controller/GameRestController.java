package demo.mable.game.controller;

import demo.mable.game.dto.GamePlayerResponse;
import demo.mable.game.service.GamePlayerService;
import demo.mable.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameRestController {

    private final GamePlayerService gamePlayerService;
    private final GameService gameService;

    @GetMapping("/games/{gameId}/players")
    public ResponseEntity<List<GamePlayerResponse>> getPlayers(@PathVariable Long gameId) {
        List<GamePlayerResponse> players = gamePlayerService.getPlayers(gameId);
        return ResponseEntity.ok().body(players);
    }

    @GetMapping("/games/{gameId}/players/count")
    public ResponseEntity<Integer> getPlayerCount(@PathVariable Long gameId) {
        int playerCount = gamePlayerService.getPlayerCount(gameId);
        return ResponseEntity.ok(playerCount);
    }
}
