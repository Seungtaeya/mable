package demo.mable.character.dto;

import demo.mable.skill.entity.CharacterSkill;

public record SkillResponse (
        String imagePath,
        String skillDescription
){
    public static SkillResponse from(CharacterSkill skill) {
        return new SkillResponse(skill.getSkill().getImagePath(),skill.getSkill().getSkillDescription());
    }
}
