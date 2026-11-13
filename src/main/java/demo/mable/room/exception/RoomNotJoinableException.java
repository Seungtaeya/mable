package demo.mable.room.exception;

public class RoomNotJoinableException extends RuntimeException {
    public RoomNotJoinableException() {
        super("방에 입장할 수 없습니다.");
    }
}
