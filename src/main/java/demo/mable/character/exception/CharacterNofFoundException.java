package demo.mable.character.exception;

public class CharacterNofFoundException extends RuntimeException {
    public CharacterNofFoundException() {
        super("존재하지 않는 캐릭터 입니다.");
    }
}
