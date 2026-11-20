package demo.mable.game.exception;

public class InvalidTileException extends RuntimeException {
    public InvalidTileException() {
        super("잘못된 타일 입니다.");
    }
}
