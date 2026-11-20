package demo.mable.game.exception;

public class AuthenticatedPlayerMismatchException extends RuntimeException {
    public AuthenticatedPlayerMismatchException() {
        super("로그인한 플레이어랑 요청한 플레이어가 다릅니다.");
    }
}
