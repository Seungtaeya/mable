package demo.mable.game.skill;

import demo.mable.skill.entity.Skill;
import demo.mable.skill.enums.SkillCode;

public class TollFreePassThenSkillHandler {

    public SkillCode supportedSkillCode() {
        return SkillCode.TOLL_EXEMPTION_THEN_WORLD_TRAVEL;
    }

    public boolean apply(int activationRate) {
        int randomRate = (int) (Math.random() * 99) + 1;

        if(randomRate <= activationRate) {
            return true;
        }
        return false;
    }
}
