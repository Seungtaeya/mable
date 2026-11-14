package demo.mable.game.service;

import demo.mable.character.dto.SkillResponse;
import demo.mable.game.dto.FirstTurnResponse;
import demo.mable.game.dto.GamePlayerDetailResponse;
import demo.mable.game.dto.GamePlayerResponse;
import demo.mable.game.entity.Game;
import demo.mable.game.entity.GamePlayer;
import demo.mable.game.entity.GameProperties;
import demo.mable.game.entity.TurnOrder;
import demo.mable.game.enums.GamePhase;
import demo.mable.game.exception.GameNotFoundException;
import demo.mable.game.exception.GamePhaseException;
import demo.mable.game.exception.GamePlayerNotFoundException;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.repository.GamePropertiesRepository;
import demo.mable.game.repository.GameRepository;
import demo.mable.game.repository.TurnOrderRepo;
import demo.mable.luckyItem.dto.EquipItemResponse;
import demo.mable.luckyItem.repository.UserSelectLuckyItemRepository;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GamePlayerService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    public final TurnOrderRepo turnOrderRepo;
    private final UserRepository userRepository;
    private final GamePropertiesRepository gamePropertiesRepository;
    private final UserSelectLuckyItemRepository userSelectLuckyItemRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<GamePlayerResponse> getPlayers(Long gameId) {
        return gamePlayerRepository.findPlayerSummariesByGameId(gameId);
    }

    public int getPlayerCount(Long gameId) {
        List<GamePlayer> byGameId = gamePlayerRepository.findByGame_Id(gameId);
        return byGameId.size();
    }

    public GamePlayerDetailResponse findPlayerDetailByUserIdAndPlayerId(Long playerId) {

        GamePlayerDetailResponse playerDetails = gamePlayerRepository.findPlayerDetailByPlayerId(playerId);
        List<SkillResponse> characterSkills = gamePlayerRepository.findCharacterSkillsByCharacterId(playerDetails.getCharacterId());
        List<EquipItemResponse> equipItems = userSelectLuckyItemRepository.findEquipItemByUserId(playerDetails.getUserId());
        playerDetails.setCharacterSkills(characterSkills);
        playerDetails.setEquipItems(equipItems);

        return playerDetails;
    }

    public FirstTurnResponse getCurrentPlayerIdByGameId(Long gameId, Integer firstCardIndex) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(gameId);

        GamePlayer gamePlayer = players.stream().filter(player -> game.getCurrentPlayerId().equals(player.getId())).findFirst().orElseThrow(GamePlayerNotFoundException::new);
        return new FirstTurnResponse(firstCardIndex, gamePlayer.getUser().getId(), gamePlayer.getId());
    }

    public GamePlayerResponse getPlayerById(Long playerId) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(playerId).orElseThrow(GamePlayerNotFoundException::new);
        return GamePlayerResponse.from(gamePlayer);
    }

    // 턴을 정하는 카드를 다 선택 했는지 보고, 게임 상태를 검사 아직 턴을 정하는 중이면
    // 첫 턴을 정하는 메서드 호출해서 첫 턴을 정하고 시계방향으로 턴을 설정 하도록 했음
    // 이제 화면에서 모든 플레이어가 턴을 정하는 카드를 선택 했다는걸 서버에 알려주도록만 하면 됨
    // 그리고 나서 턴을 기준으로 플레이어들이 주사위를 던지는 코드를 작성하면 됨
    @Transactional
    public Integer determineTurnOrderIfAllPlayersSelected(Long gameId, String userName, int turnOrder) {
        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(gameId);
        List<TurnOrder> turnOrders = turnOrderRepo.findByGame_Id(gameId);


        if(players.size() != turnOrders.size()) {
            saveTurnOrderSelection(gameId, userName, turnOrder);
        }

        if(hasAllPlayersSelectedTurnOrderCards(gameId)) {
            return determineTurnOrder(gameId);
        }
        return null;
    }

    public int determineTurnOrder(Long gameId) {

        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);

        if(!game.getPhase().equals(GamePhase.TURN_ORDER_SELECTING)) {
            throw new GamePhaseException("이미 턴 확정 했습니다.");
        }

        List<TurnOrder> turn = turnOrderRepo.findByGame_Id(gameId);

        int firstCardIndex = turn.get((int)(Math.random() * turn.size())).getTurn();

        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(gameId);

        for(int i = 0; i < turn.size(); i++) {
            Long selectedUserId = turn.get(i).getUser().getId();
            int selectedCardIdx = turn.get(i).getTurn();

            int finalTurnOrder = (selectedCardIdx - firstCardIndex + players.size()) % players.size();

            System.out.println("GamePlayerId: " + players.get(i).getUser().getId() + " userId: " + selectedUserId + " turnOrder: " + players.get(i).getTurnOrder() + " finalTurnOrder: " + finalTurnOrder);
            for(int j = 0; j < players.size(); j++) {
                if(selectedUserId.equals(players.get(j).getUser().getId())) {
                    players.get(j).modifyTurnOrder(finalTurnOrder);
                    break;
                }
            }

        }

        game.changePhase(GamePhase.Waiting_FOR_DICE);
        GamePlayer currentPlayer = players.stream().filter(player -> player.getTurnOrder().equals(0)).findFirst().orElseThrow(() -> new IllegalArgumentException("determineTurnOrder Exception"));
        game.modifyCurrentPlayerId(currentPlayer.getId());
        return firstCardIndex;
    }

    public void saveTurnOrderSelection(Long gameId,String userName, int turnOrder ) {

        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        User user = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(gameId);

        boolean isPlayer = players.stream().anyMatch(player -> player.getUser().getId().equals(user.getId()));

        if(!isPlayer) {
            throw new GamePlayerNotFoundException();
        }

        boolean existsByGameIdAndUsername = turnOrderRepo.existsByGame_IdAndUser_Username(gameId, userName);
        if(existsByGameIdAndUsername) {
            throw new IllegalArgumentException("이미 카드를 선택 했습니다.");
        }

        TurnOrder turn = new TurnOrder(turnOrder);
        turn.assignGame(game);
        turn.assignUser(user);

        turnOrderRepo.save(turn);

    }

    public Long validatePlayerIsOnTile(Long gameId, Integer tileId, String userName) {
        GamePlayer player = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, tileId);

        if(player.getPosition() != tileId) {
            throw new IllegalArgumentException("플레이어가 타일 위에 없습니다");
        }

        if(tile.isLandmark()) {

            throw new IllegalArgumentException("랜드마크는 인수 불가능");
        }
        return player.getId();
    }

    private boolean hasAllPlayersSelectedTurnOrderCards(Long gameId) {

        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(gameId);
        List<TurnOrder> turnOrders = turnOrderRepo.findByGame_Id(gameId);

        return players.size() == turnOrders.size();
    }
}
