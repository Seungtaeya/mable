package demo.mable.game.skill;

import demo.mable.game.dto.DiceRollResult;
import demo.mable.luckyItem.entity.UserSelectedLuckyItem;
import demo.mable.skill.enums.SkillCode;

public class DiceControlSkillHandler implements DiceRollSkillHandler{


    @Override
    public SkillCode supportedSkillCode() {
        return SkillCode.DICE_CONTROL_ACCURACY_BONUS;
    }

    @Override
    public DiceRollResult applyDiceControl(int left, int right, int targetSum, int activationRate, UserSelectedLuckyItem userSelectedLuckyItem) {
        if(!(targetSum >= 2) && (targetSum <= 12)){
            return null;
        }

        if((left + right) == targetSum) {
            return new DiceRollResult(left, right, false, "");
        }
        int randomRate = (int) ((Math.random() * 99) +1);

        if(randomRate <= activationRate) {
            int minLeft = Math.max(1, targetSum - 6);
            int maxLeft = Math.min(6, targetSum - 1);
            int count = maxLeft - minLeft + 1;

            int leftDice = (int) (Math.random() * count) + minLeft;
            int rightDice = targetSum - leftDice;

            return new DiceRollResult(leftDice, rightDice, true, userSelectedLuckyItem.getUserLuckyItem().getLuckyItem().getSkill().getImagePath());
        }
        return new DiceRollResult(left, right, false ,"");
    }
}
