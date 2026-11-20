package demo.mable.game.exception;

public class NextTurnPlayerNotFoundException extends RuntimeException {
    public NextTurnPlayerNotFoundException() {
        super("다음 턴을 진행할 플레이어를 찾지 못했습니다.");
    }
}
