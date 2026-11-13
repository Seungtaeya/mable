package demo.mable.skill.dto;

import demo.mable.skill.enums.SkillCode;
import demo.mable.skill.enums.TriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCharacterSkillRequest {

    @NotNull
    private SkillCode skillCode;
    @NotBlank
    private String skillName;

    @NotBlank
    private String skillDescription;

    @NotNull
    private TriggerType triggerType;

    @NotNull
    private Long characterId;

    @NotNull
    private Integer activationRate;

    @NotNull
    private Integer effectValue;

    @NotNull
    private Integer displayOrder;
}