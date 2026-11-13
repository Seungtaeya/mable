package demo.mable.room.service;

import demo.mable.chat.repository.MessageRepository;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.repository.GameRepository;
import demo.mable.game.repository.TurnOrderRepo;
import demo.mable.room.dto.*;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import demo.mable.room.enums.GameRoomStatus;
import demo.mable.room.exception.AlreadyJoinedRoomException;
import demo.mable.room.exception.GameRoomNotFoundException;
import demo.mable.room.exception.RoomFullException;
import demo.mable.room.exception.RoomNotJoinableException;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.room.repository.RoomPlayerRepository;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageRepository messageRepository;

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final TurnOrderRepo turnOrderRepo;

    @Transactional
    public Long createGameRoom(Long hostUser, GameRoomRequest request) {

        if(request.title() == null) {
            throw new IllegalArgumentException("방 제목은 필수값 입니다.");
        }
        User user = userRepository.findById(hostUser).orElseThrow(UserNotFoundException::new);

        GameRoom gameRoom = new GameRoom();

        gameRoom.createRoom(request.title(), user, request.maxPlayers());

        GameRoom save = gameRoomRepository.save(gameRoom);
        createRoomPlayer(user,gameRoom,0);

        eventPublisher.publishEvent(new RoomCreatedEvent(GameRoomResponse.from(save)));
        return save.getId();
    }

    @Transactional
    public JoinRoomResponse joinRoom(Long roomId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);

        if(gameRoom.getStatus() != GameRoomStatus.WAITING) {
            throw new RoomNotJoinableException();
        }

        if(roomPlayerRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new AlreadyJoinedRoomException();
        }

        List<RoomPlayer> players = roomPlayerRepository.findAllByRoomIdOrderByGameOrderAsc(roomId);

        if(players.size() >= gameRoom.getMaxPlayers()) {
            throw new RoomFullException();
        }

        int turnOrder = findAvailableTurnOrder(players, gameRoom.getMaxPlayers());

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        gameRoom.addPlayerCount();
        RoomPlayer roomPlayer = createRoomPlayer(user,gameRoom,turnOrder);


        return new JoinRoomResponse(roomPlayer.getRoom().getId(), roomPlayer.getGameOrder());
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        gameRoomRepository.delete(gameRoom);
    }

    @Transactional
    public void clearAllRoomsForTest() {
        turnOrderRepo.deleteAll();;
        gamePlayerRepository.deleteAll();
        gameRepository.deleteAll();
        messageRepository.deleteAll();
        roomPlayerRepository.deleteAll();
        gameRoomRepository.deleteAll();
    }

    @Transactional
    public void closeRoomIfEmpty(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        if(gameRoom.getPlayerCount() == 0 ) {
            gameRoom.changeStatus(GameRoomStatus.CLOSED);
        }
    }

    public boolean isUserHost(Long roomId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);

        return gameRoom.isHost(userId);
    }

    private int findAvailableTurnOrder(List<RoomPlayer> players, int maxPlayers) {
        Set<Integer> usedTurn = players.stream()
                .map(RoomPlayer::getGameOrder)
                .collect(Collectors.toSet());

        return IntStream.range(0, maxPlayers)
                .filter(order -> !usedTurn.contains(order))
                .findFirst()
                .orElseThrow(RoomFullException::new);
    }

    private RoomPlayer createRoomPlayer(User user, GameRoom gameRoom, int turnOrder) {
        RoomPlayer roomPlayer = new RoomPlayer();
        roomPlayer.addUser(user);
        roomPlayer.assignRoom(gameRoom);
        roomPlayer.modifyGameOrder(turnOrder);
        return roomPlayerRepository.save(roomPlayer);
    }

    public List<GameRoomResponse> getRooms() {
//        List<GameRoom> rooms = gameRoomRepository.findAllByStatus(GameRoomStatus.WAITING);
        List<GameRoom> rooms = gameRoomRepository.findAllOrderByDesc();
        return rooms.stream()
                .map(GameRoomResponse::from)
                .toList();
    }

    public GameRoomResponse getGameRoom(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        return GameRoomResponse.from(gameRoom);
    }


}
