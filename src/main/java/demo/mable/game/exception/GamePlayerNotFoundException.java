package demo.mable.game.exception;

public class GamePlayerNotFoundException extends RuntimeException {
    public GamePlayerNotFoundException() {
        super("게임중인 플레이어가 아닙니다.");
    }
}
