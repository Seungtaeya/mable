package demo.mable.room.exception;

public class AlreadyJoinedRoomException extends RuntimeException {
    public AlreadyJoinedRoomException() {
        super("이미 입장한 방 입니다.");
    }
}
