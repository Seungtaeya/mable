package demo.mable.game.service;

import demo.mable.game.entity.Game;
import demo.mable.game.entity.GamePlayer;
import demo.mable.game.enums.GamePhase;
import demo.mable.game.enums.GameStatus;
import demo.mable.game.enums.MapCode;
import demo.mable.game.enums.PlayerStatus;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.repository.GameRepository;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import demo.mable.room.enums.GameRoomStatus;
import demo.mable.room.exception.GameRoomNotFoundException;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.room.repository.RoomPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameStartService {

    private final GameRepository gameRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GameRoomRepository gameRoomRepository;

    @Transactional
    public Game startGame(Long roomId, Long hostUserId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        List<RoomPlayer> players = roomPlayerRepository.findByRoomId(roomId);

        if(!gameRoom.getStatus().equals(GameRoomStatus.WAITING)) {
            throw new IllegalStateException("게임 시작은 대기방일때만 가능합니다.");
        }

        if(!players.stream().allMatch(RoomPlayer::isGameReady)) {
            throw new IllegalStateException("모든 플레이어가 레디해야 게임을 시작 할 수 있습니다.");
        }

        if(!gameRoom.isHost(hostUserId)) {
            throw new IllegalStateException("호스트가 다릅니다.");
        }
//        if(gameRoom.getPlayerCount() <= 1) {
//            throw new IllegalStateException("최소 인원수가 맞지 않습니다.");
//        }

        gameRoom.changeStatus(GameRoomStatus.PLAYING);

        Game game = new Game(MapCode.world, GameStatus.PLAYING, GamePhase.TURN_ORDER_SELECTING,0,null, LocalDateTime.now().plusSeconds(30));
        game.assignRoom(gameRoom);
        Game save = gameRepository.save(game);

        for (RoomPlayer player : players) {
            GamePlayer gamePlayer = new GamePlayer(null, PlayerStatus.ACTIVE,10000000L,0,0,0,0);
            gamePlayer.assignGame(save);
            gamePlayer.addUser(player.getUser());
            gamePlayerRepository.save(gamePlayer);
        }

        return save;
    }
}
