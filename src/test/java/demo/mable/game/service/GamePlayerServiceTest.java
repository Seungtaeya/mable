package demo.mable.game.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GamePlayerServiceTest {

    @Autowired GamePlayerService gamePlayerService;

    @Test
    public void findPlayerDetailsByPlayerId() throws Exception {
        //given
        gamePlayerService.findPlayerDetailByUserIdAndPlayerId(1157L);
        //when
        
        //then
    }
}