package demo.mable.game.exception;

public class InsufficientPlayersException extends RuntimeException {
    public InsufficientPlayersException() {
        super("최소 인원수가 맞지 않습니다.");
    }
}
