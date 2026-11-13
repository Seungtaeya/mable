package demo.mable.game.service;

import demo.mable.game.dto.PlayerTollPaymentRequest;
import demo.mable.game.dto.TollCalculatedResponse;
import demo.mable.game.entity.GamePlayer;
import demo.mable.game.entity.GameProperties;
import demo.mable.game.enums.PlayerStatus;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.repository.GamePropertiesRepository;
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
class GameServiceTest {

    
    @Autowired GameService gameService;
    @Autowired
    GamePlayerRepository gamePlayerRepository;
    @Autowired GamePropertiesRepository propertiesRepository;
    @Test
    public void pay() throws Exception {
        //given
        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(355L);
        PlayerTollPaymentRequest request = new PlayerTollPaymentRequest();
        request.setPlayerId(616L);
        request.setTileIndex(21);
        //when
//        TollCalculatedResponse tollCalculatedResponse = gameService.tollPayment(355L, request);
        List<GameProperties> properties = propertiesRepository.findByGame_IdAndPlayer_Id(355L, 616L);

        //then
//        assertThat(tollCalculatedResponse.getTollPlayerStatus().name()).isEqualTo(PlayerStatus.BANKRUPT.name());
        assertThat(properties.size()).isEqualTo(0);
    }
}