package demo.mable.room.exception;

public class RoomFullException extends RuntimeException {
    public RoomFullException() {
        super("풀방 입니다.");
    }
}
