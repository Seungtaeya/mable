package demo.mable.character.exception;

public class UserIdRequiredException extends RuntimeException {
    public UserIdRequiredException() {
        super("userId는 null일 수 없습니다.");
    }
}
