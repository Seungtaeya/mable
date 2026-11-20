package demo.mable.game.exception;

public class InvalidMovementPathException extends RuntimeException {
    public InvalidMovementPathException() {
        super("잘못된 이동경로 입니다.");
    }
}
