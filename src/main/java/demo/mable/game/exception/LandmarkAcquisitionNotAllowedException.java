package demo.mable.game.exception;

public class LandmarkAcquisitionNotAllowedException extends RuntimeException
{
    public LandmarkAcquisitionNotAllowedException() {
        super("랜드마크는 인수 불가능");
    }
}
