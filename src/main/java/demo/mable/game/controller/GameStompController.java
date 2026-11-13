package demo.mable.game.controller;

import demo.mable.game.dto.*;
import demo.mable.game.repository.GamePlayerRepository;
import demo.mable.game.service.GamePlayerService;
import demo.mable.game.service.GameService;
import demo.mable.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class GameStompController {
    private final GamePlayerService gamePlayerService;
    private final GamePlayerRepository gamePlayerRepository;
    private final GameService gameService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/game/{gameId}/players/turnOrder")
    public void selectPlayerTurnCardDisabled(@DestinationVariable Long gameId, @Payload TurnOrderSelectRequest request, Principal principal) throws InterruptedException {
        Integer firstCardIndex = gamePlayerService.determineTurnOrderIfAllPlayersSelected(gameId, principal.getName(), request.getSelectTurn());

        if(firstCardIndex == null) {
            Long userId = userService.getUserIdByUsername(principal.getName());
            PlayerSelectTurnOrderResponse response = new PlayerSelectTurnOrderResponse(userId, request.getSelectTurn());
            simpMessagingTemplate.convertAndSend("/topic/games/"+ gameId + "/turn-order/button-selected", response);
        } else {
            FirstTurnResponse firstTurnResponse = gamePlayerService.getCurrentPlayerIdByGameId(gameId, firstCardIndex);
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-order-determined", firstTurnResponse);

        }
    }

    @MessageMapping("/games/{gameId}/dice-roll")
    public void rollDice(@DestinationVariable Long gameId, @Payload Integer targetSum, Principal principal) {
        PlayerMoveResult playerMoveResult = gameService.movePlayerCharacter(gameId, targetSum, principal.getName());

        simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/move", playerMoveResult.move());
        if(playerMoveResult.nextRoll() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-changed", playerMoveResult.nextRoll());
        }
    }

    @MessageMapping("/games/{gameId}/players/me/building-purchase")
    public void buildingPurchase(@DestinationVariable Long gameId,  @Payload BuildingPurchaseRequest request ) {
        BuildingPurchaseResult result = gameService.buildingPurchase(gameId, request);

        simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/building-purchased", result.building());
        if(result.skillMove() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/skill-move", result.skillMove());
        }
        if(result.nextRoll() != null){
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-changed", result.nextRoll());
        }
    }

    @MessageMapping("/games/{gameId}/player/me/toll-payment")
    public void tollPay(@DestinationVariable Long gameId, @Payload PlayerTollPaymentRequest request, Principal principal) {
        System.out.println(" tollPayment Call ");

        TollCalculatedResult result = gameService.tollPayment(gameId, principal.getName(), request);

        if(result.toll() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/toll-calculated",result.toll());
        }
        if(result.response() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/"+ gameId + "/train-travel", result.response());
        }
        if(result.nextRoll() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/"+gameId+"/turn-changed", result.nextRoll());
        }
        if(result.gameResultResponse() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/"+gameId+"/game-finished", result.gameResultResponse() );
        }
    }

    @MessageMapping("/games/{gameId}/player/me/building-acquire")
    public void acquireBuilding(@DestinationVariable Long gameId, @Payload BuildingAcquireRequest request) {
        BuildingAcquireResult result = gameService.acquireBuilding(gameId, request);
        if(result.acquiredResponse() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/building-acquired", result.acquiredResponse());
        }
        simpMessagingTemplate.convertAndSend("/topic/games/"+gameId+"/turn-changed", result.nextRoll());
    }

    @MessageMapping("/games/{gameId}/players/{playerId}/building-skip")
    public void buildingSkip(@DestinationVariable Long gameId, @DestinationVariable Long playerId, Principal principal) {
        TurnChangedResponse turnChangedResponse = gameService.buildingSkip(gameId, principal.getName(), playerId);
        simpMessagingTemplate.convertAndSend("/topic/games/"+gameId+"/turn-changed", turnChangedResponse);
    }

    @MessageMapping("/games/{gameId}/players/{playerId}/building-upgrade-skip")
    public void buildingUpgradeSkip(@DestinationVariable Long gameId, @DestinationVariable Long playerId, Principal principal) {
        TurnChangedResponse turnChangedResponse = gameService.buildingUpgradeSkip(gameId, principal.getName(), playerId);
        simpMessagingTemplate.convertAndSend("/topic/games/"+gameId+"/turn-changed", turnChangedResponse);
    }

    @MessageMapping("/games/{gameId}/players/me/skill-building-purchase")
    public void skillBuildingPurchase(@DestinationVariable Long gameId,  @Payload BuildingPurchaseRequest request, Principal principal) {
        BuildingPurchaseResult result = gameService.skillBuildingPurchase(gameId, principal.getName(), request);
        if(result.building() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/skill-building-purchase", result.building());
        }
        if(result.skillMove() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/skill-move", result.skillMove());
        } else if(result.nextRoll() != null){
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-changed", result.nextRoll());
        }
    }

    @MessageMapping("/games/{gameId}/players/me/building-upgrade")
    public void buildingUpgrade(@DestinationVariable Long gameId,  @Payload BuildingPurchaseRequest request, Principal principal) {
        BuildingPurchaseResult result = gameService.buildingUpgrade(gameId, principal.getName(), request);
        if(result.building() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/skill-building-purchase", result.building());
        }
        if(result.skillMove() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/skill-move", result.skillMove());
        } else if(result.nextRoll() != null){
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-changed", result.nextRoll());
        }
    }

    @MessageMapping("/games/{gameId}/players/me/train-travel-select")
    public void trainTravelTileSelect(@DestinationVariable Long gameId, @Payload TrainTravelTileRequest request, Principal principal) {
        PlayerMoveResult playerMoveResult = gameService.selectTrainDestination(gameId, principal.getName(), request.getTileId());
        if(playerMoveResult.move() != null) {
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/train-travel-arrived", playerMoveResult.move());
        }
        if(playerMoveResult.nextRoll() != null){
            simpMessagingTemplate.convertAndSend("/topic/games/" + gameId + "/turn-changed", playerMoveResult.nextRoll());
        }
    }
}
