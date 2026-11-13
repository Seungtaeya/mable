package demo.mable.game.service;

import demo.mable.character.entity.UserSelectedCharacter;
import demo.mable.character.exception.UserSelectedCharacterNotFoundException;
import demo.mable.character.repository.UserSelecteCharacterRespository;
import demo.mable.game.dto.*;
import demo.mable.game.entity.Game;
import demo.mable.game.entity.GamePlayer;
import demo.mable.game.entity.GameProperties;
import demo.mable.game.entity.GameResult;
import demo.mable.game.enums.GamePhase;
import demo.mable.game.enums.GameStatus;
import demo.mable.game.enums.PlayerStatus;
import demo.mable.game.enums.TileType;
import demo.mable.game.exception.*;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.repository.GamePropertiesRepository;
import demo.mable.game.repository.GameRepository;
import demo.mable.game.repository.GameResultRepository;
import demo.mable.game.skill.ConstructBuildingSkillHandler;
import demo.mable.game.skill.DiceControlSkillHandler;
import demo.mable.game.skill.DiceRollSkillHandler;
import demo.mable.game.skill.TollFreePassThenSkillHandler;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import demo.mable.luckyItem.exception.LuckyItemNotFoundException;
import demo.mable.luckyItem.repository.UserSelectLuckyItemRepository;
import demo.mable.skill.enums.SkillCode;
import demo.mable.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GamePropertiesRepository gamePropertiesRepository;
    private final UserSelecteCharacterRespository userSelecteCharacterRespository;
    private final UserSelectLuckyItemRepository userSelectLuckyItemRepository;
    private final GameResultRepository gameResultRepository;

    @Transactional
    public PlayerMoveResult movePlayerCharacter(Long gameId, Integer targetSum, String userName){
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer currentPlayer = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);
        List<BoardTileDefinition> tiles = Map.TILES;
        List<String> itemImage = new ArrayList<>();

        if(!game.getPhase().equals(GamePhase.Waiting_FOR_DICE)) {
            throw new GameNotWaitingForDiceException();
        }
        if(!game.getCurrentPlayerId().equals(currentPlayer.getId())) {
            throw new IllegalArgumentException("현재 턴 플레이어가 아닙니다.");
        }
        List<UserSelectedLuckyItem> luckyItems = userSelectLuckyItemRepository.findByUser_Id(currentPlayer.getUser().getId());
        DiceRollSkillHandler diceRollSkillHandler = new DiceControlSkillHandler();

        UserSelectedLuckyItem item = luckyItems.stream()
                .filter(luckyItem ->
                        diceRollSkillHandler.supportedSkillCode().equals(luckyItem.getUserLuckyItem().getLuckyItem().getSkill().getSkillCode()))
                .findFirst().orElse(null);

        int dice1 = (int) ((Math.random() * 6) + 1);
        int dice2 = (int) ((Math.random() * 6) + 1);

        DiceRollResult diceRollResult = new DiceRollResult(dice1, dice2, false, null);

        // 현재 플레이어가 0번 타일을 주사위로 굴려서 지나가면 지나간 플레이어에게 돈 지급
        if((currentPlayer.getPosition() + (diceRollResult.left() + diceRollResult.right()) > 31)) {
            currentPlayer.updataDiposit(1000000);
        }

        if(item != null) {
            diceRollResult = diceRollSkillHandler.applyDiceControl(dice1, dice2, targetSum,100,item );
//            diceRollResult = diceRollSkillHandler.applyDiceControl(dice1, dice2, targetSum, item.getUserLuckyItem().getLuckyItem().getActivationRate(),item );
        }

        if(diceRollResult.UserLuckyItemImagePath() != null && diceRollResult.activated()) {
            itemImage.add(diceRollResult.UserLuckyItemImagePath());
        }

        if(diceRollResult.left() == diceRollResult.right()) {
            currentPlayer.updateConsecutiveDoubles();

            if(currentPlayer.getConsecutiveDoubles() == 3){

                currentPlayer.movePlayerToLand(8);
                currentPlayer.initConsecutiveDoubles();

                prepareNextRoll(game, currentPlayer);

                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), 8, null, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(), itemImage);

                GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
                TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());

                return new PlayerMoveResult(response,nextPlayerResponse);

            }
            currentPlayer.sumPosition(diceRollResult.left() + diceRollResult.right());
        } else {
            currentPlayer.initConsecutiveDoubles();
            currentPlayer.sumPosition(diceRollResult.left() + diceRollResult.right());

        }


        boolean existsTile = gamePropertiesRepository.existsByGame_IdAndTileId(gameId, currentPlayer.getPosition());
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, currentPlayer.getPosition());


        if(currentPlayer.getPosition() == 0 || currentPlayer.getPosition() == 8 || currentPlayer.getPosition() == 16 || currentPlayer.getPosition() == 24) {

            // 현재 플레이어가 기차여행에 있을 경우 TurnChangedResponse -> NextAction에 Train_Travel추가 돔
            if(currentPlayer.getPosition() == 24) {
                game.changePhase(GamePhase.WAITING_FOR_TRAIN_DESTINATION);
                List<Integer> list = tiles.stream().map(BoardTileDefinition::pathIndex).toList();
                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), currentPlayer.getPosition(),existsTile, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(),itemImage);
                TurnChangedResponse turnChangedResponse = TurnChangedResponse.fromTrainTravel(currentPlayer.getUser().getId(), currentPlayer.getId(), "TRAIN_TRAVEL", list);
                return new PlayerMoveResult(response, turnChangedResponse);
            }
            prepareNextRoll(game, currentPlayer);
            return buildPlayerMoveResult(game,currentPlayer,existsTile,diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(),itemImage);

        } else if (currentPlayer.getPosition() == 2 || currentPlayer.getPosition() == 12 || currentPlayer.getPosition() == 20 || currentPlayer.getPosition() == 28 || currentPlayer.getPosition() == 30) {

            int randomDestination = (int) (Math.random() * 32);

            boolean randomDestinationExistsTile = gamePropertiesRepository.existsByGame_IdAndTileId(gameId, randomDestination);

            GameProperties randomTile = gamePropertiesRepository.findByGameIdAndTileId(gameId, randomDestination);
            BoardTileDefinition landedTile = Map.TILES.stream()
                    .filter(board -> board.pathIndex() == randomDestination)
                    .findFirst()
                    .orElseThrow(TileNotFoundException::new);
            currentPlayer.movePlayerToLand(randomDestination);


            if (randomDestination == 2 || randomDestination == 12 || randomDestination == 20 || randomDestination == 28 || randomDestination == 30 || randomDestination == 0|| randomDestination == 8|| randomDestination == 16|| randomDestination == 24) {
                prepareNextRoll(game, currentPlayer);

                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), randomDestination, null,diceRollResult.left(), diceRollResult.right(),diceRollResult.activated(), itemImage);

                GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);

                TurnChangedResponse nextRoll = TurnChangedResponse.fromRandomDestination(nextPlayer.getUser().getId(), nextPlayer.getId(), "TRAP");

                return new PlayerMoveResult(response, nextRoll);
            }

            if(!randomDestinationExistsTile) {
                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), randomDestination, false, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(), itemImage);
                game.changePhase(GamePhase.WAITING_FOR_BUILDING);
                return new PlayerMoveResult(response, null);
            } else if (randomTile.getPlayer().getId().equals(currentPlayer.getId())) {
                if(!randomTile.isLandmark() && !landedTile.type().equals(TileType.TOURIST_SPOT)) {
                    PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), randomDestination,null, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(),itemImage);
                    response.setLandingAction("UPGRADE");
                    return new PlayerMoveResult(response, null);
                }
            } else if(!randomTile.getPlayer().getId().equals(currentPlayer.getId())) {
                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), randomDestination,true, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(),itemImage);
                game.changePhase(GamePhase.WAITING_FOR_TOLL_PAYMENT);
                return new PlayerMoveResult(response, null);

            } else {
                prepareNextRoll(game, currentPlayer);
                PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), randomDestination, null, diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(), itemImage);
                TurnChangedResponse turnChangedResponse = TurnChangedResponse.fromRandomDestination(currentPlayer.getUser().getId(), game.getCurrentPlayerId(), "TRAP");
                return new PlayerMoveResult(response, turnChangedResponse);
            }
        }else if(!existsTile) {
            game.changePhase(GamePhase.WAITING_FOR_BUILDING);
            PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), currentPlayer.getPosition(), false, diceRollResult.left() , diceRollResult.right(), diceRollResult.activated(), itemImage);
            return new PlayerMoveResult(response, null);
        } else if(tile.getPlayer().getId().equals(currentPlayer.getId())) {
            BoardTileDefinition landedTile = Map.TILES.stream()
                    .filter(board -> board.pathIndex() == currentPlayer.getPosition())
                    .findFirst()
                    .orElseThrow(TileNotFoundException::new);

            if(landedTile.type().equals(TileType.TOURIST_SPOT)) {
                prepareNextRoll(game, currentPlayer);
                return buildPlayerMoveResult(game,currentPlayer, null,diceRollResult.left(), diceRollResult.right(), diceRollResult.activated(), itemImage);
            }

            PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), currentPlayer.getPosition(), null, diceRollResult.left() , diceRollResult.right(), diceRollResult.activated(), itemImage);

            if(!tile.isLandmark()) {
                game.changePhase(GamePhase.WAITING_FOR_SKILL_BUILD_TARGET);
                response.setLandingAction("UPGRADE");
                GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
//                TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
                return new PlayerMoveResult(response, null);
            }

            prepareNextRoll(game,currentPlayer);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
            return new PlayerMoveResult(response, nextPlayerResponse);
        }
        else if(!Objects.equals(tile.getPlayer().getId(), currentPlayer.getId())) {
            game.changePhase(GamePhase.WAITING_FOR_TOLL_PAYMENT);
            PlayerRollResponse response = PlayerRollResponse.from(game.getId(), currentPlayer.getId(), currentPlayer.getPosition(), true, diceRollResult.left() , diceRollResult.right(), diceRollResult.activated(), itemImage);
            return new PlayerMoveResult(response, null);
        }

        prepareNextRoll(game, currentPlayer);
        return buildPlayerMoveResult(game,currentPlayer, null,diceRollResult.left() , diceRollResult.right(), diceRollResult.activated(),itemImage);
    }

    private PlayerMoveResult buildPlayerMoveResult(Game game, GamePlayer player, Boolean existsTile,int dice1, int dice2, boolean diceControlActivated,List<String> itemImage){
        PlayerRollResponse response = PlayerRollResponse.from(game.getId(), player.getId(), player.getPosition(),existsTile, dice1, dice2, diceControlActivated,itemImage);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
        return new PlayerMoveResult(response, nextPlayerResponse);
    }

    @Transactional
    public BuildingPurchaseResult buildingPurchase(Long gameId, @NotNull BuildingPurchaseRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer gamePlayer = gamePlayerRepository.findById(request.getPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        GameProperties gameProperties = new GameProperties();

        List<BoardTileDefinition> tiles = Map.TILES;

        long constructionCost = 0;

        game.changePhase(GamePhase.WAITING_FOR_BUILDING);

        if(gamePropertiesRepository.existsByGame_IdAndTileId(gameId,request.getTileId())) {
            throw new GamePropertyAlreadyExistsException();
        }

        gameProperties.assignGame(game);
        gameProperties.assignGamePlayer(gamePlayer);
        gameProperties.addTile(request.getTileId());
        BoardTileDefinition boardTile = tiles.stream().filter(tile -> tile.pathIndex() == request.getTileId()).findFirst().orElseThrow(TileNotFoundException::new);

        if(request.getBuildingLevels().isEmpty()) {
            gameProperties.constructionFlag(true);
            constructionCost = (constructionCost + boardTile.landPrice());
        } else {
            for (Integer buildingLevel : request.getBuildingLevels()) {
                if (buildingLevel == 1) {
                    gameProperties.constructionHouse(true);
                    constructionCost = (long) (constructionCost + (boardTile.landPrice() * 0.4));
                } else if (buildingLevel == 2) {
                    gameProperties.constructionVilla(true);
                    constructionCost = (long) (constructionCost + (boardTile.landPrice() * 0.6));
                } else if( buildingLevel == 3) {
                    gameProperties.constructionApartment(true);
                    constructionCost += boardTile.landPrice();
                } else if(buildingLevel == 4) {
                    gameProperties.constructionLandmark(true);
                }
            }
        }

        gameProperties.updateConstructionCost(constructionCost);
        gamePlayer.payment(constructionCost);
        GameProperties save = gamePropertiesRepository.save(gameProperties);
        BoardTileDefinition boardTileDefinition = tiles.stream()
                .filter(tile -> tile.pathIndex() == request.getTileId())
                .findFirst()
                .orElseThrow(TileNotFoundException::new);
        List<UserSelectedLuckyItem> userLuckyItems = userSelectLuckyItemRepository.findByUser_Id(gamePlayer.getUser().getId());

        // 소지아이템
        for (UserSelectedLuckyItem userLuckyItem : userLuckyItems) {
            if(userLuckyItem.getUserLuckyItem().getLuckyItem().getSkill().getSkillCode().equals(SkillCode.MOVE_TO_START_AFTER_BUILD)) {
                ConstructBuildingSkillHandler skillHandler = new ConstructBuildingSkillHandler();
                BuildingPurchasedResponse buildingPurchasedResponse = new BuildingPurchasedResponse(gamePlayer.getId(), gamePlayer.getCash(), request.getTileId(), save.isFlag(), save.isHouse(), save.isVilla(), save.isApartment(), save.isLandmark());

                if (!boardTileDefinition.type().equals(TileType.TOURIST_SPOT)) {
                    // 건설 장비 확률 테스트용 100
                boolean apply = skillHandler.apply(userLuckyItem.getUserLuckyItem().getLuckyItem().getActivationRate());
//                boolean apply = skillHandler.apply(0);
//                    boolean apply = skillHandler.apply(100);
                    if (apply) {
                        List<Integer> properties = gamePropertiesRepository.findPropertiesMissingAnyBuilding(gameId, gamePlayer.getId());
                        gamePlayer.movePlayerToLand(0);
                        game.changePhase(GamePhase.WAITING_FOR_SKILL_BUILD_TARGET);
                        SkillMoveResponse response = new SkillMoveResponse(gamePlayer.getId(), 0, true, userLuckyItem.getUserLuckyItem().getLuckyItem().getSkill().getImagePath(), properties);
                        return new BuildingPurchaseResult(buildingPurchasedResponse, null, response);
                    }
                }
            }
        }
        prepareNextRoll(game, gamePlayer);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        BuildingPurchasedResponse buildingPurchasedResponse = new BuildingPurchasedResponse(gamePlayer.getId(), gamePlayer.getCash(), request.getTileId(), save.isFlag(), save.isHouse(), save.isVilla(), save.isApartment(), save.isLandmark());
        TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());

        return new BuildingPurchaseResult(buildingPurchasedResponse,nextRoll, null);
    }

    private void prepareNextRoll(Game game, GamePlayer player) {
        if(player.getConsecutiveDoubles() == 0) {
            turnChange(game.getId(),player.getUser().getUsername());
        }
        game.changePhase(GamePhase.Waiting_FOR_DICE);
    }

    @Transactional
    public TollCalculatedResult tollPayment(Long gameId, String userName, @NotNull PlayerTollPaymentRequest request ) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer tollPayer = gamePlayerRepository.findByGameIdAndUsername(gameId,userName);

        if(!game.getPhase().equals(GamePhase.WAITING_FOR_TOLL_PAYMENT)) {
            throw new IllegalArgumentException("현재 게임상태가 요청한 메서드가 원하는 게임 상태가 아닙니다.");
        }
        if(!tollPayer.getId().equals(request.getPlayerId())) {
            throw new IllegalArgumentException("로그인한 플레이어랑 요청한 플레이어가 다릅니다.");
        }
        if(!game.getCurrentPlayerId().equals(tollPayer.getId())) {
            throw new IllegalArgumentException("현재턴 플레이어랑 요청자랑 다릅니다 확인해주세요.");
        }
        if(!(tollPayer.getPosition() == request.getTileIndex())) {
            throw new IllegalArgumentException("플레이어가 위치한 타일 번호랑 요청으로 넘어온 타일 번호가 같지않습니다.");
        }

        TollFreePassThenSkillHandler handler = new TollFreePassThenSkillHandler();
        UserSelectedLuckyItem item = userSelectLuckyItemRepository.findByUser_Id(tollPayer.getUser().getId()).stream()
                .filter(luckyItem -> luckyItem.getUserLuckyItem().getLuckyItem().getSkill().getSkillCode().equals(handler.supportedSkillCode()))
                .findFirst().orElse(null);

        boolean apply = false;

        if(item != null) {
            apply = handler.apply(item.getUserLuckyItem().getLuckyItem().getActivationRate());
//        apply = handler.apply(100);
        }

        GameProperties tileInfo = gamePropertiesRepository.findByGameIdAndTileId(gameId, request.getTileIndex());
        UserSelectedCharacter userSelectedCharacter = userSelecteCharacterRespository.findByUserId(tollPayer.getUser().getId()).orElseThrow(UserSelectedCharacterNotFoundException::new);
        GamePlayer tileOwner = tileInfo.getPlayer();

        if(apply) {
            game.changePhase(GamePhase.WAITING_FOR_TRAIN_DESTINATION);
            List<Integer> tilesIdx = Map.TILES.stream().map(BoardTileDefinition::pathIndex).toList();
            tollPayer.movePlayerToLand(24);
            PlayerMoveResponse response = new PlayerMoveResponse(gameId, tollPayer.getId(), 24, true,item.getUserLuckyItem().getLuckyItem().getSkill().getImagePath());
            TurnChangedResponse travelSelection = new TurnChangedResponse(tollPayer.getUser().getId(), tollPayer.getId(),"TRAIN_TRAVEL",tilesIdx ) ;
            return new TollCalculatedResult(null,travelSelection, response, null);
        }

        if(tileOwner.getId().equals(tollPayer.getId())) {
            throw new IllegalArgumentException("땅 주인은 통행료를 낼 수 없습니다.");
        }
        // 통행료 계산
        double tollPaymentRate = userSelectedCharacter.getUserCharacter().getCharacter().getTollDiscount() / 100.0;
        long totalToPay = (long) ((tileInfo.getConstructionCost() / 10) * tollPaymentRate);
        System.out.println("totalToPay = " + totalToPay);

        boolean isLandMark = tileInfo.isLandmark();
        // 파산 시 땅 주인은 땅 가격 그대로 입금, 땅 밟은사람은 파산
        if(tollPayer.getCash() < totalToPay) {
            tollPayer.payment(tollPayer.getCash());
            tollPayer.updatePlayerStatus(PlayerStatus.BANKRUPT);
            tollPayer.initConsecutiveDoubles();
            tileOwner.updataDiposit(totalToPay);
            gamePropertiesRepository.deleteAllByPlayer_Id(tollPayer.getId());

            GameResultResponse gameFinish = finishGameIfOnlyOnePlayerRemains(game);
            TollCalculatedResponse BankruptResponse = TollCalculatedResponse.from(tileInfo.getTileId(), tileOwner.getId(), tileOwner.getCash(), tollPayer.getId(), tollPayer.getCash(), tollPayer.getPlayerStatus(), isLandMark);

            if(gameFinish != null) {
                return new TollCalculatedResult(BankruptResponse, null, null,gameFinish);
            }

            prepareNextRoll(game,tollPayer);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId()) ;
            return new TollCalculatedResult(BankruptResponse, nextPlayerResponse,null, null);
        } else {
            tollPayer.payment(totalToPay);
            tileOwner.updataDiposit(totalToPay);
        }
        TollCalculatedResponse response = TollCalculatedResponse.from(tileInfo.getTileId(), tileOwner.getId(), tileOwner.getCash(), tollPayer.getId(), tollPayer.getCash(), tollPayer.getPlayerStatus(), isLandMark);
        game.changePhase(GamePhase.WAITING_FOR_BUILDING_ACQUISITION);

        // 랜드마크 면 턴 바꾸고 종료
        if(tileInfo.isLandmark()) {
            prepareNextRoll(game,tollPayer);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId()) ;
            return new TollCalculatedResult(response,nextPlayerResponse,null, null);
        }
        return new TollCalculatedResult(response,null,null, null);
    }

    @Transactional
    public BuildingAcquireResult acquireBuilding(Long gameId, BuildingAcquireRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);

        if(!game.getPhase().equals(GamePhase.WAITING_FOR_BUILDING_ACQUISITION)) {
            throw new IllegalArgumentException("현재 게임상태가 요청한 메서드가 원하는 게임 상태가 아닙니다.");
        }
        GamePlayer player = gamePlayerRepository.findById(request.getPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, request.getTileId());
        GamePlayer oldOwner = tile.getPlayer();
        UserSelectedCharacter userSelectedCharacter = userSelecteCharacterRespository.findByUserId(player.getUser().getId()).orElseThrow(UserNotFoundException::new);

        if(player.getPosition() != request.getTileId()) {
            throw new IllegalArgumentException("플레이어가 타일 위에 없습니다");
        }

        long acquisitionCost = tile.getConstructionCost() * 2;
        double acquisitionCostDiscount = userSelectedCharacter.getUserCharacter().getCharacter().getAcquisitionCostDiscount() /100.0;
        long totalAcquisitionCost = (long) (acquisitionCost * acquisitionCostDiscount);

        if(player.getCash() < totalAcquisitionCost) {
            prepareNextRoll(game,player);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId())
                    .orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());
            return new BuildingAcquireResult(null, nextRoll);
        }

        player.payment(totalAcquisitionCost);
        oldOwner.updataDiposit(totalAcquisitionCost);
        tile.changeTileOwner(player);
        prepareNextRoll(game, player);

        BuildingAcquiredResponse acquired = new BuildingAcquiredResponse(tile.getTileId(), oldOwner.getId(), oldOwner.getCash(), player.getId(), player.getCash());

        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId())
                .orElseThrow(GamePlayerNotFoundException::new);

        TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());

        return new BuildingAcquireResult(acquired, nextRoll);
    }

    @Transactional
    public TurnChangedResponse buildingSkip(Long gameId, String userName, Long playerId) {
        return skipConstruction(gameId, userName, playerId);
    }

    @Transactional
    public TurnChangedResponse buildingUpgradeSkip(Long gameId, String userName, Long playerId) {
        return skipConstruction(gameId,userName,playerId);
    }

    @NotNull
    private TurnChangedResponse skipConstruction(Long gameId, String userName, Long playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer player = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);

        if(!player.getId().equals(playerId)) {
            throw new IllegalArgumentException("게임에 참여한 플레이어와 요청보낸 플레이어가 다릅니다,");
        }
        prepareNextRoll(game,player);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        return new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());
    }

    @Transactional
    public void turnChange(Long gameId, String userName) {
        List<GamePlayer> players = gamePlayerRepository.findByGame_IdOrderByTurnOrderAsc(gameId);
        GamePlayer currentPlayer = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);

        for(int i = 0; i < players.size(); i++) {
            GamePlayer gamePlayer = players.get(i);

            if (!game.getCurrentPlayerId().equals(gamePlayer.getId())) {
                continue;
            }

           for(int offset = 1; offset < players.size(); offset++) {
               GamePlayer nextPlayer = players.get((i + offset) % players.size());
               if(nextPlayer.getPlayerStatus().equals(PlayerStatus.BANKRUPT))
                   continue;

               game.changePhase(GamePhase.Waiting_FOR_DICE);
               game.modifyCurrentPlayerId(nextPlayer.getId());
//               return new TurnChangedResponse(currentPlayer.getUser().getId(), nextPlayer.getId());
           }
        }
//        throw new IllegalStateException("다음 턴을 진행할 플레이어를 찾지 못했습니다.");
    }

    @Transactional
    public BuildingPurchaseResult skillBuildingPurchase(Long gameId, String userName, BuildingPurchaseRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer player = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, request.getTileId());

        valid(request, game, player, tile);

        long landPrice = Map.TILES.stream()
                .filter(boardTile -> boardTile.pathIndex() == request.getTileId())
                .findFirst()
                .orElseThrow(TileNotFoundException::new)
                .landPrice();

        long constructionCost = 0;

        for (Integer level : request.getBuildingLevels()) {
            long price = (long) switch (level) {
                case 1 -> (long) landPrice * 0.4;
                case 2 -> landPrice * 0.6;
                case 3 -> landPrice;
                case 4 -> tile.getConstructionCost() * 2L;
                default -> throw new IllegalArgumentException("잘못된 건물 레벨입니다.");
            };

            constructionCost += price;
        }

        if(player.getCash() >= constructionCost) {
            player.payment(constructionCost);
        } else {
            BuildingPurchasedResponse response = new BuildingPurchasedResponse(player.getId(), player.getCash(),
                    tile.getTileId(), tile.isFlag(), tile.isHouse(), tile.isVilla(), tile.isApartment(), tile.isLandmark());

            prepareNextRoll(game, player);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());
            return new BuildingPurchaseResult(response, nextRoll, null);
        }

        for (Integer buildingLevel : request.getBuildingLevels()) {
            if(buildingLevel == 1) {
                if(!tile.isHouse()) {
                    tile.constructionHouse(true);
                }
            } else if(buildingLevel == 2) {
                if(!tile.isVilla()) {
                    tile.constructionVilla(true);
                }
            } else if(buildingLevel == 3) {
                if(!tile.isApartment()) {
                    tile.constructionApartment(true);
                }
            } else if(buildingLevel == 4) {
                if(tile.isHouse() && tile.isVilla() && tile.isApartment()) {
                    tile.constructionLandmark(true);
                }
            }
        }

        tile.updateConstructionCost(tile.getConstructionCost() + constructionCost);
        BuildingPurchasedResponse response = new BuildingPurchasedResponse(player.getId(), player.getCash(),
                tile.getTileId(), tile.isFlag(), tile.isHouse(), tile.isVilla(), tile.isApartment(), tile.isLandmark());

        prepareNextRoll(game, player);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());

        return new BuildingPurchaseResult(response, nextRoll, null);
    }

    @Transactional
    public BuildingPurchaseResult buildingUpgrade(Long gameId, String userName, BuildingPurchaseRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer player = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, request.getTileId());

        List<BoardTileDefinition> tiles = Map.TILES;
        long landPrice = tiles.stream().filter(boardTile -> boardTile.pathIndex() == request.getTileId())
                .findFirst()
                .orElseThrow(TileNotFoundException::new)
                .landPrice();

        long constructionCost = 0;
        for (Integer buildingLevel : request.getBuildingLevels()) {
            switch (buildingLevel) {
                case 4: constructionCost = landPrice * 2; break;
            }

        }
        if(player.getCash() >= constructionCost) {
            player.payment(constructionCost);
        } else  {
            BuildingPurchasedResponse response = new BuildingPurchasedResponse(player.getId(), player.getCash(),
                    tile.getTileId(), tile.isFlag(), tile.isHouse(), tile.isVilla(), tile.isApartment(), tile.isLandmark());

            prepareNextRoll(game, player);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());
            return new BuildingPurchaseResult(response, nextRoll, null);
        }


        for (Integer buildingLevel : request.getBuildingLevels()) {
            if(buildingLevel == 4) {
                if(tile.isHouse() && tile.isVilla() && tile.isApartment()) {
                    tile.constructionLandmark(true);
                }
            }
        }

        tile.updateConstructionCost(tile.getConstructionCost() + constructionCost);
        BuildingPurchasedResponse response = new BuildingPurchasedResponse(player.getId(), player.getCash(),
                tile.getTileId(), tile.isFlag(), tile.isHouse(), tile.isVilla(), tile.isApartment(), tile.isLandmark());

        prepareNextRoll(game, player);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        TurnChangedResponse nextRoll = new TurnChangedResponse(nextPlayer.getUser().getId(), nextPlayer.getId());

        return new BuildingPurchaseResult(response, nextRoll, null);
    }

    @Transactional
    public PlayerMoveResult  selectTrainDestination(Long gameId, String userName, Integer tileId) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        GamePlayer player = gamePlayerRepository.findByGameIdAndUsername(gameId, userName);

        if(!game.getPhase().equals(GamePhase.WAITING_FOR_TRAIN_DESTINATION)) {
            throw new IllegalArgumentException("게임 상태가 GamePhase.WAITING_FOR_TRAIN_DESTINATION 아닙니다.");
        }
        if(!game.getCurrentPlayerId().equals(player.getId())) {
            throw new IllegalArgumentException("현재 접속한 플레이어는 현재 턴의 플레이어가 아닙니다.");
        }
        if(!(tileId >= 0 && tileId <= 31)) {
            throw new IllegalArgumentException("잘못된 이동경로 입니다.");
        }

        player.movePlayerToLand(tileId);

        boolean existsTile = gamePropertiesRepository.existsByGame_IdAndTileId(gameId, player.getPosition());
        GameProperties tile = gamePropertiesRepository.findByGameIdAndTileId(gameId, player.getPosition());

        if(player.getPosition() == 0 || player.getPosition() == 8 || player.getPosition() == 16 || player.getPosition() == 24) {

            prepareNextRoll(game, player);
            return TrainTravelMoveResult(game,player.getId(),player.getPosition(),false);

        } else if (player.getPosition() == 2 || player.getPosition() == 12 || player.getPosition() == 20 || player.getPosition() == 28 || player.getPosition() == 30) {

            prepareNextRoll(game, player);
            return TrainTravelMoveResult(game,player.getId(),player.getPosition(),false);

        }else if(!existsTile) {
            game.changePhase(GamePhase.WAITING_FOR_BUILDING);
            PlayerRollResponse response = PlayerRollResponse.forTrainTravel(gameId, player.getId(), tileId, false);
            return new PlayerMoveResult(response, null);
        } else if(tile.getPlayer().getId().equals(player.getId())) {
            BoardTileDefinition landedTile = Map.TILES.stream()
                    .filter(board -> board.pathIndex() == player.getPosition())
                    .findFirst()
                    .orElseThrow(TileNotFoundException::new);

            PlayerRollResponse response = PlayerRollResponse.forTrainTravel(game.getId(), player.getId(), player.getPosition(), null);

            if(landedTile.type().equals(TileType.TOURIST_SPOT)) {
                prepareNextRoll(game, player);
                return TrainTravelMoveResult(game, player.getId(), player.getPosition(),null);
            }
            game.changePhase(GamePhase.WAITING_FOR_SKILL_BUILD_TARGET);

            if(!tile.isLandmark()) {
                game.changePhase(GamePhase.WAITING_FOR_SKILL_BUILD_TARGET);
                response.setLandingAction("UPGRADE");
                GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
//                TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
                return new PlayerMoveResult(response, null);
            }

            prepareNextRoll(game,player);
            GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
            TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
            return new PlayerMoveResult(response, nextPlayerResponse);
        }
        else if(!Objects.equals(tile.getPlayer().getId(), player.getId())) {
            game.changePhase(GamePhase.WAITING_FOR_TOLL_PAYMENT);
            PlayerRollResponse response = PlayerRollResponse.forTrainTravel(game.getId(), player.getId(), player.getPosition(), true);
            return new PlayerMoveResult(response, null);
        }
        prepareNextRoll(game, player);
        return TrainTravelMoveResult(game,player.getId(),player.getPosition(),true);
    }

    private GameResultResponse finishGameIfOnlyOnePlayerRemains(Game game) {
        List<GamePlayer> players = gamePlayerRepository.findByGame_Id(game.getId());
        int activeCount = 0;

        for (GamePlayer player : players) {
            if(player.getPlayerStatus().equals(PlayerStatus.ACTIVE)) {
                activeCount += 1;
            }
        }

        if(activeCount == 1) {
            GamePlayer gamePlayer = players.stream()
                    .filter(player -> player.getPlayerStatus().equals(PlayerStatus.ACTIVE))
                    .findFirst().orElse(null);

            if(gamePlayer != null) {
                GameResult gameResult = new GameResult(1,true,gamePlayer.getCash(),"파산 승리");
                gameResult.assignGame(game);
                gameResult.assignUser(gamePlayer.getUser());

                GameResult save = gameResultRepository.save(gameResult);
                game.updateFinishReason("파산");
                game.updateGameStatus(GameStatus.FINISHED);
                game.ended();
                return new GameResultResponse(gamePlayer.getUser().getUsername(), save.getRank_no(), gamePlayer.getCash(), "파산 승리");
            }
        }

        return null;
    }

    private PlayerMoveResult TrainTravelMoveResult(Game game, Long playerId, Integer tileId, Boolean existsTile) {
        PlayerRollResponse response = PlayerRollResponse.forTrainTravel(game.getId(),playerId, tileId,existsTile);
        GamePlayer nextPlayer = gamePlayerRepository.findById(game.getCurrentPlayerId()).orElseThrow(GamePlayerNotFoundException::new);
        TurnChangedResponse nextPlayerResponse = new TurnChangedResponse(nextPlayer.getUser().getId(),nextPlayer.getId());
        return new PlayerMoveResult(response, nextPlayerResponse);
    }

    private static void valid(BuildingPurchaseRequest request, Game game, GamePlayer player, GameProperties tile) {
        if(!game.getPhase().equals(GamePhase.WAITING_FOR_SKILL_BUILD_TARGET)) {
            throw new IllegalArgumentException("GamePhase.WAITING_FOR_SKILL_BUILD_TARGET이 아닙니다.");
        }
        if(player == null) {
            throw new IllegalArgumentException("플레이어가 없습니다.");
        }
        if(tile == null) {
            throw new IllegalArgumentException("잘못된 타일 입니다.");
        }
        if(!game.getCurrentPlayerId().equals( player.getId())) {
            throw new IllegalArgumentException("현재 턴인 플레이어가 아닙니다.");
        }
        if(!tile.getPlayer().getId().equals(player.getId())) {
            throw new IllegalArgumentException("선택한 타일의 주인이 아닙니다.");
        }
        if(request.getBuildingLevels().isEmpty()) {
            throw new IllegalArgumentException("건설할 건물을 선택하지 않았습니다.");
        }

        for (Integer buildingLevel : request.getBuildingLevels()) {
            switch (buildingLevel){
                case 1: if(tile.isHouse()) throw new IllegalArgumentException("이미 지어진 건물은 지을 수 없습니다."); break;
                case 2: if(tile.isVilla()) throw new IllegalArgumentException("이미 지어진 건물은 지을 수 없습니다."); break;
                case 3: if(tile.isApartment()) throw new IllegalArgumentException("이미 지어진 건물은 지을 수 없습니다."); break;
                case 4: if(tile.isLandmark()) throw new IllegalArgumentException("이미 지어진 건물은 지을 수 없습니다."); break;
            }
        }
    }
}

