package demo.mable.game.exception;

public class BuildingAlreadyConstructedException extends RuntimeException {
    public BuildingAlreadyConstructedException() {
        super("이미 지어진 건물은 지을 수 없습니다.");
    }
}
