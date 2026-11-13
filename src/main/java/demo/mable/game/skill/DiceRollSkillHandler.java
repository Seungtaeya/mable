package demo.mable.game.skill;

import demo.mable.game.dto.DiceRollResult;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import demo.mable.skill.enums.SkillCode;

public interface DiceRollSkillHandler {
    SkillCode supportedSkillCode();
    DiceRollResult applyDiceControl(int left, int right, int targetSum, int activationRate, UserSelectedLuckyItem userSelectedLuckyItem);
}
