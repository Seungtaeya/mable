package demo.mable.character.exception;

public class CharacterRankMismatchException extends RuntimeException {
    public CharacterRankMismatchException() {
        super("캐릭터 합성시 등급이 같아야합니다.");
    }
}
