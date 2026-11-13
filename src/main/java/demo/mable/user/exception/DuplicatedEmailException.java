package demo.mable.user.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException() {
        super("이미 사용중인 이메일 입니다.");
    }
}
