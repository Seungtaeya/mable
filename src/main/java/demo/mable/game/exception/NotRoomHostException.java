package demo.mable.game.exception;

public class NotRoomHostException extends RuntimeException {
    public NotRoomHostException() {
        super("호스트가 다릅니다.");
    }
}
