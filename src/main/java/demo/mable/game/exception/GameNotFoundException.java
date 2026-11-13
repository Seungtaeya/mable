package demo.mable.game.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException() {
        super("게임이 존재하지 않습니다.");
    }
}
