package demo.mable.game.dto;

public record TollCalculatedResult(TollCalculatedResponse toll, TurnChangedResponse nextRoll, PlayerMoveResponse response, GameResultResponse gameResultResponse) {
}
