package demo.mable.game.exception;

public class InvalidBuildingLevelException extends RuntimeException {
    public InvalidBuildingLevelException() {
        super("잘못된 건물 레벨입니다.");
    }
}
