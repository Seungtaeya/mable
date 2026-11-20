package demo.mable.game.exception;

public class PropertyOwnerCannotPayTollException extends RuntimeException {
    public PropertyOwnerCannotPayTollException() {
        super("땅 주인은 통행료를 낼 수 없습니다.");
    }
}
