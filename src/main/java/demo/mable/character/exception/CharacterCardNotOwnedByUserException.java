package demo.mable.character.exception;

public class CharacterCardNotOwnedByUserException extends RuntimeException {
    public CharacterCardNotOwnedByUserException() {
        super("해당 캐릭터 카드를 가지고 있지 않습니다.");
    }
}
