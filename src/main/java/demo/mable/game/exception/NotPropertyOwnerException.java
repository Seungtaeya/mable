package demo.mable.game.exception;

public class NotPropertyOwnerException extends RuntimeException {
    public NotPropertyOwnerException() {
        super("선택한 타일의 주인이 아닙니다.");
    }
}
