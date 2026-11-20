package demo.mable.game.exception;

public class NotCurrentTurnPlayerException extends RuntimeException {
    public NotCurrentTurnPlayerException() {
        super("현재 턴 플레이어가 아닙니다.");
    }
}
