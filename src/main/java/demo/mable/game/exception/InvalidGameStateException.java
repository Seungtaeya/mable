package demo.mable.game.exception;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException() {
        super("현재 게임상태가 요청한 메서드가 원하는 게임 상태가 아닙니다.");
    }
}
