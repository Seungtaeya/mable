package demo.mable.game.exception;

public class GameNotWaitingForDiceException extends RuntimeException {
    public GameNotWaitingForDiceException() {
        super("GamePhase가 WaitingForDice가 아닙니다.");
    }
}
