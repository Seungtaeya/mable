package demo.mable.character.exception;

public class CharacterIdRequiredException extends RuntimeException {
    public CharacterIdRequiredException() {
        super("characterId는 null이여선 안됩니다.");
    }
}
