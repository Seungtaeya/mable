package demo.mable.character.exception;

public class UserCharacterLimitExceededException extends RuntimeException {
  public UserCharacterLimitExceededException() {
    super("보관함이 가득 찼습니다.");
  }
}
