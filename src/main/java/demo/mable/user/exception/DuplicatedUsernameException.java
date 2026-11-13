package demo.mable.user.exception;

public class DuplicatedUsernameException extends RuntimeException {
    public DuplicatedUsernameException() {
        super("이미 사용 중인 아이디 입니다.");
    }
}
