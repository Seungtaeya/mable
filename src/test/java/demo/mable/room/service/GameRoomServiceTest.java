package demo.mable.room.service;

import demo.mable.auth.dto.SignupRequest;
import demo.mable.auth.service.AuthService;
import demo.mable.room.dto.GameRoomRequest;
import demo.mable.room.dto.JoinRoomResponse;
import demo.mable.room.dto.RoomPlayerJoinRequest;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.room.repository.RoomPlayerRepository;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class GameRoomServiceTest {

    @Autowired GameRoomService gameRoomService;
    @Autowired RoomPlayerRepository roomPlayerRepository;
    @Autowired GameRoomRepository gameRoomRepository;
    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;

    Long hostId;
    @BeforeEach
    void registerUser() {
        SignupRequest signupRequest = new SignupRequest("test1","123","123","aaa@aaa.ccc");
        hostId = authService.registerUser(signupRequest);

    }
    @BeforeEach
    void createRoom() {
        GameRoomRequest gameRoomRequest = new GameRoomRequest("testRoom",4);
        User user = userRepository.findByUsername("test1").orElseThrow(() -> new UserNotFoundException());

        gameRoomService.createGameRoom(user.getId(),gameRoomRequest);
    }

    @Test
    public void assignTurnOrderWhenUserJoinRoom() throws Exception {
        //given
        List<GameRoom> testRoom = gameRoomRepository.findByTitle("testRoom");
        SignupRequest signupRequest = new SignupRequest("joinUser","123","123","bbb@aaa.ccc");
        Long userId = authService.registerUser(signupRequest);
        RoomPlayerJoinRequest roomPlayerJoinRequest = new RoomPlayerJoinRequest(testRoom.getFirst().getId());
        //when
        JoinRoomResponse joinRoomResponse = gameRoomService.joinRoom(roomPlayerJoinRequest.roomId(),userId);
        //then
        assertThat(joinRoomResponse.roomId()).isEqualTo(testRoom.getFirst().getId());
        assertThat(joinRoomResponse.turnOrder()).isEqualTo(0);
        assertThat(testRoom.getFirst().getHostUser().getId()).isEqualTo(hostId);

    }
    
    @Test
    public void getCurrentRoomPlayerCount() throws Exception {
        //given
        List<GameRoom> testRoom = gameRoomRepository.findByTitle("testRoom");
        SignupRequest signupRequest = new SignupRequest("joinUser","123","123","bbb@aaa.ccc");
        Long userId = authService.registerUser(signupRequest);
        RoomPlayerJoinRequest roomPlayerJoinRequest = new RoomPlayerJoinRequest(testRoom.getFirst().getId());
        //when
        gameRoomService.joinRoom(roomPlayerJoinRequest.roomId(),userId);
        List<RoomPlayer> allByRoomIdOrderByGameOrderAsc = roomPlayerRepository.findAllByRoomIdOrderByGameOrderAsc(testRoom.getFirst().getId());
        //then
        assertThat(allByRoomIdOrderByGameOrderAsc.size()).isEqualTo(2);
        for (RoomPlayer roomPlayer : allByRoomIdOrderByGameOrderAsc) {
            System.out.println(roomPlayer.getUser().toString() + "turnOrder" + roomPlayer.getGameOrder());
        }
    }
}