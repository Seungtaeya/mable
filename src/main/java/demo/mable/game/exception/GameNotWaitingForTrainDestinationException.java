package demo.mable.game.exception;

public class GameNotWaitingForTrainDestinationException extends RuntimeException {
    public GameNotWaitingForTrainDestinationException() {
        super("게임 상태가 GamePhase.WAITING_FOR_TRAIN_DESTINATION 아닙니다.");
    }
}
