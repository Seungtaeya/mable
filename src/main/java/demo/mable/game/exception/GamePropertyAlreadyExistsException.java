package demo.mable.game.exception;

public class GamePropertyAlreadyExistsException extends RuntimeException {
    public GamePropertyAlreadyExistsException() {
        super("해당 타일의 건물이 건설 돼있습니다.");
    }
}
