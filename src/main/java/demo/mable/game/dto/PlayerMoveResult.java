package demo.mable.game.dto;

public record PlayerMoveResult(PlayerRollResponse move, TurnChangedResponse nextRoll) {
}
