package demo.mable.game.exception;

public class PlayerNotOnTileException extends RuntimeException {
    public PlayerNotOnTileException() {
        super("플레이어가 타일 위에 없습니다");
    }
}
