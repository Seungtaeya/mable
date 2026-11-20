package demo.mable.game.exception;

public class GameRoomNotWaitingException extends RuntimeException {
    public GameRoomNotWaitingException() {
        super("게임 시작은 대기방일때만 가능합니다.");
    }
}
