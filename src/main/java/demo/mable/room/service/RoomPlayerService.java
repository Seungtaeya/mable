package demo.mable.room.service;

import demo.mable.game.entity.Game;
import demo.mable.game.service.GameStartService;
import demo.mable.room.dto.*;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import demo.mable.room.enums.GameRoomStatus;
import demo.mable.room.enums.RoomActionType;
import demo.mable.room.exception.*;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.room.repository.RoomPlayerRepository;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import demo.mable.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomPlayerService {

    private final RoomPlayerRepository roomPlayerRepository;
    private final GameRoomRepository gameRoomRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final GameStartService gameStartService;

    public List<GameWaitingRoomPlayerResponse> getRoomPlayers(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);

        if(gameRoom.getClosed() != null) {
            throw new GameRoomClosedException();
        }

        List<RoomPlayer> players = roomPlayerRepository.findByRoomId(roomId);
        return players.stream().map((player) -> new GameWaitingRoomPlayerResponse(player.getUser().getId(),player.getUser().getUsername(),gameRoom.isHost(player.getUser().getId()), player.isGameReady())).toList();
    }

    @Transactional
    public int leaveRoom(Long roomId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);

        if(gameRoom.getStatus() == GameRoomStatus.PLAYING) {
            throw new IllegalArgumentException("게임을 시작해서 나갈 수 없습니다.");
        }

        RoomPlayer player = roomPlayerRepository.findByRoom_IdAndUser_Id(roomId, userId).orElseThrow(RoomPlayerNotFoundException::new);

        if(player.isGameReady()) {
            throw new IllegalArgumentException("레디를 풀어야 나갈 수 있습니다.");
        }

        roomPlayerRepository.delete(player);
        gameRoom.decrementPlayerCount();

        if(gameRoom.getPlayerCount() == 0) {
            gameRoom.closeRoom();
        }

        if(gameRoom.isHost(userId)) {
            String hostUserName = changeHostUser(roomId, gameRoom);
            eventPublisher.publishEvent(new GameRoomChangeHostEvent(roomId,hostUserName));
        }

        eventPublisher.publishEvent(new LeaveRoom(gameRoom.getId()));
        return gameRoom.getPlayerCount();
    }

    @Transactional
    public RoomActionResponse gameAction(Long roomId, String userName) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        User user = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        RoomPlayer roomPlayer = roomPlayerRepository.findByRoom_IdAndUser_Id(roomId, user.getId()).orElseThrow(RoomPlayerNotFoundException::new);



        roomPlayer.toggleReady();

        return new RoomActionResponse(roomId, user.getId(), RoomActionType.READY_CHANGED, roomPlayer.isGameReady());
    }

    @Transactional
    public GameStartedResponse startAction(Long roomId, String userName) {

        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        User user = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        RoomPlayer roomPlayer = roomPlayerRepository.findByRoom_IdAndUser_Id(roomId, user.getId()).orElseThrow(RoomPlayerNotFoundException::new);

        if(!gameRoom.isHost(user.getId())) {
            throw new IllegalStateException("방장만 게임을 시작 할 수 있습니다.");
        }

        // 게임 시작
        roomPlayer.toggleReady();
        Game startGame = gameStartService.startGame(roomId, user.getId());
        return new GameStartedResponse(startGame.getId(), roomId, user.getId(), RoomActionType.GAME_STARTED, roomPlayer.isGameReady());
    }

    private String changeHostUser(Long roomId, GameRoom gameRoom) {
        List<RoomPlayer> players = roomPlayerRepository.findByRoomId(roomId);
        String hostUserName = "";
        if(!players.isEmpty()) {
            RoomPlayer firstPlayer = players.getFirst();
            User firstPlayerUser = firstPlayer.getUser();
            gameRoom.changeHostUser(firstPlayerUser);
            hostUserName = firstPlayerUser.getUsername();
        }
        return hostUserName;
    }
}
