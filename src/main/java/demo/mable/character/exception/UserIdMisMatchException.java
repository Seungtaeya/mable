package demo.mable.character.exception;

public class UserIdMisMatchException extends RuntimeException {
    public UserIdMisMatchException() {
        super("아이디가 일치하지 않습니다.");
    }
}
