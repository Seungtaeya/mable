package demo.mable.game.skill;

public class ConstructBuildingSkillHandler {


    public boolean apply(int activationRate) {

        int randomRate = (int) (Math.random() * 99) + 1;

        if(activationRate >= randomRate) {
            return true;
        }

        return false;
    }
}
