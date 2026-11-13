package demo.mable.luckyItem.exception;

public class LuckyItemNotFoundException extends RuntimeException {
    public LuckyItemNotFoundException() {
        super("아이템을 찾을 수 없습니다.");
    }
}
