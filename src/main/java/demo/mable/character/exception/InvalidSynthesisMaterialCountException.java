package demo.mable.character.exception;

public class InvalidSynthesisMaterialCountException extends RuntimeException {
    public InvalidSynthesisMaterialCountException() {
        super("합성에 필요한 캐릭터는 2개입니다.");
    }
}
