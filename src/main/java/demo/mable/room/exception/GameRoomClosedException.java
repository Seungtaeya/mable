package demo.mable.room.exception;

public class GameRoomClosedException extends RuntimeException {
    public GameRoomClosedException() {
        super("대기방이 닫혔습니다.");
    }
}
