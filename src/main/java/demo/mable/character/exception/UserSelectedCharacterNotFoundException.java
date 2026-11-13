package demo.mable.character.exception;

public class UserSelectedCharacterNotFoundException extends RuntimeException {
    public UserSelectedCharacterNotFoundException() {
        super("장착한 캐릭터가 없습니다.");
    }
}
