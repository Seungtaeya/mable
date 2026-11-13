package demo.mable.luckyItem.dto;

import demo.mable.skill.enums.SkillCode;
import demo.mable.skill.enums.TriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLuckyItemRequest (
        @NotNull
        SkillCode skillCode,
        @NotNull
        TriggerType triggerType,
        @NotBlank
        String skillDescription,
        @NotBlank
        String itemName,
        @NotBlank
        String itemRank,
        Integer activationRate){
}
