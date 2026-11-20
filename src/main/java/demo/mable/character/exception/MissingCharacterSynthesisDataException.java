package demo.mable.character.exception;

public class MissingCharacterSynthesisDataException extends RuntimeException {
    public MissingCharacterSynthesisDataException() {
        super("합성에 필요한 데이터가 없습니다.");
    }
}
