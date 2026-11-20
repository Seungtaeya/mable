package demo.mable.game.exception;

public class GamePhaseException extends RuntimeException {
    public GamePhaseException() {
        super("이미 턴 확정 했습니다.");
    }
}
