package demo.mable.game.exception;

public class TileNotFoundException extends RuntimeException {
    public TileNotFoundException() {
        super("해당 타일 아이디로 찾을 수 없습니다.");
    }
}
