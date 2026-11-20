package demo.mable.character.exception;

public class UnsupportedCharacterRankException extends RuntimeException {
    public UnsupportedCharacterRankException() {
        super("합성을 지원하지 않는 등급입니다.");
    }
}
