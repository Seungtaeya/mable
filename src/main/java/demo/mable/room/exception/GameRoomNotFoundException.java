package demo.mable.room.exception;

public class GameRoomNotFoundException extends RuntimeException {
    public GameRoomNotFoundException() {
        super("방을 찾을 수 없습니다.");
    }
}
