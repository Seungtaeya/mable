package demo.mable.game.exception;

public class BuildingSelectionRequiredException extends RuntimeException {
    public BuildingSelectionRequiredException() {
        super("건설할 건물을 선택하지 않았습니다.");
    }
}
