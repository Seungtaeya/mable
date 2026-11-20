package demo.mable.game.exception;

public class TurnOrderCardAlreadySelectedException extends RuntimeException {
    public TurnOrderCardAlreadySelectedException() {
        super("이미 카드를 선택 했습니다.");
    }
}
