package demo.mable.game.exception;

public class NotAllPlayersReadyException extends RuntimeException {
    public NotAllPlayersReadyException() {
        super("모든 플레이어가 레디해야 게임을 시작 할 수 있습니다.");
    }
}
