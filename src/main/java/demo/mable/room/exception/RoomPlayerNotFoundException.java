package demo.mable.room.exception;

public class RoomPlayerNotFoundException extends RuntimeException {
    public RoomPlayerNotFoundException() {
        super("대기방에 플레이어가 없습니다.");
    }
}
