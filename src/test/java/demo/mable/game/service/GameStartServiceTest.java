package demo.mable.game.service;

import demo.mable.game.entity.Game;
import demo.mable.game.enums.GamePhase;
import demo.mable.game.enums.GameStatus;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.room.repository.RoomPlayerRepository;
import demo.mable.user.entity.User;
import demo.mable.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameStartServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRoomRepository gameRoomRepository;
    @Autowired
    RoomPlayerRepository roomPlayerRepository;
    @Autowired
    GameStartService gameStartService;

    Long gameRoomId = null;
    Long hostUserId = null;

    @BeforeEach
    void register() {
        User user1 = new User();
        user1.createUser("aa331aa@aa.aa","123","a1");
        userRepository.save(user1);
        User user2 = new User();
        user2.createUser("4141@aa.aa","123","a2");
        userRepository.save(user2);

        GameRoom gameRoom = new GameRoom();
        gameRoom.createRoom("test", user1,2);
        GameRoom gameRoomSave = gameRoomRepository.save(gameRoom);
        RoomPlayer roomPlayer1 = new RoomPlayer();
        roomPlayer1.assignRoom(gameRoomSave);
        roomPlayer1.addUser(user1);
        roomPlayer1.toggleReady();
        roomPlayerRepository.save(roomPlayer1);
        RoomPlayer roomPlayer2 = new RoomPlayer();
        roomPlayer2.assignRoom(gameRoomSave);
        roomPlayer2.addUser(user2);
        roomPlayer2.toggleReady();
        roomPlayerRepository.save(roomPlayer2);

        gameRoomId = gameRoomSave.getId();
        hostUserId = user1.getId();
    }

    @Test
    @Transactional
    public void gameStart() throws Exception {
        //given
        //when
        Game game = gameStartService.startGame(gameRoomId, hostUserId);

        //then
        assertThat(game.getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(game.getGameRoom().getId()).isEqualTo(gameRoomId);
        assertThat(game.getPhase()).isEqualTo(GamePhase.TURN_ORDER_SELECTING);
    }
}