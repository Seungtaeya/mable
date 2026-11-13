package demo.mable.room.service;

import demo.mable.room.dto.GameRoomRequest;
import demo.mable.room.dto.GameWaitingRoomPlayerResponse;
import demo.mable.room.dto.JoinRoomResponse;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.user.entity.User;
import demo.mable.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoomPlayerServiceTest {
    
    @Autowired RoomPlayerService roomPlayerService;
    @Autowired GameRoomService gameRoomService;
    @Autowired
    GameRoomRepository gameRoomRepository;
    @Autowired
    UserRepository userRepository;
    
    @Test
    public void leave() throws Exception {
        //given
        User user = new User();
        user.createUser("aaaa.aaa.com","123","123");
        User save = userRepository.save(user);
        User user1 = new User();
        user1.createUser("asd@asd.dd","123","qwe");
        User saveUser = userRepository.save(user1);
        GameRoomRequest gameRoomRequest = new GameRoomRequest("testRoom",2);
        Long gameRoom = gameRoomService.createGameRoom(save.getId(), gameRoomRequest);


        //when
        gameRoomService.joinRoom(gameRoom, saveUser.getId());
        List<GameWaitingRoomPlayerResponse> roomPlayers = roomPlayerService.getRoomPlayers(gameRoom);
        
        roomPlayerService.leaveRoom(gameRoom, saveUser.getId());

        List<GameWaitingRoomPlayerResponse> after_leaving_room = roomPlayerService.getRoomPlayers(gameRoom);
        //then

        assertThat(roomPlayers.size()).isEqualTo(2);
        assertThat(after_leaving_room.size()).isEqualTo(1);
    }
}