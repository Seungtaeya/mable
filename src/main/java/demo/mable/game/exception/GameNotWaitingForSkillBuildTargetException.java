package demo.mable.game.exception;

public class GameNotWaitingForSkillBuildTargetException extends RuntimeException {
    public GameNotWaitingForSkillBuildTargetException() {
        super("GamePhase.WAITING_FOR_SKILL_BUILD_TARGET이 아닙니다.");
    }
}
