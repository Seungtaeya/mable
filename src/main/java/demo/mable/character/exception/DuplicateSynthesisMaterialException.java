package demo.mable.character.exception;

public class DuplicateSynthesisMaterialException extends RuntimeException {
    public DuplicateSynthesisMaterialException() {
        super("같은 캐릭터로 합성은 불가능 합니다.");
    }
}
