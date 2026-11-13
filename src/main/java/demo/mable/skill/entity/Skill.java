package demo.mable.skill.entity;

import demo.mable.skill.enums.SkillCode;
import demo.mable.skill.enums.TriggerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_code" , length = 20)
    @Enumerated(EnumType.STRING)
    private SkillCode skillCode;
    @Column(name = "trigger_type", length = 20)
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;
    @Column(name = "image_path", length = 100)
    private String imagePath;
    @Column(name = "skill_name", length = 20)
    private String skillName;
    @Column(name = "skill_description", length = 100)
    private String skillDescription;
    @Column(name = "is_active")
    private boolean isActive;

    public Skill(SkillCode skillCode, TriggerType triggerType, String skillName, String skillDescription, String image_path) {
        this.skillCode = skillCode;
        this.skillName = skillName;
        this.skillDescription = skillDescription;
        this.triggerType = triggerType;
        this.imagePath = image_path;
    }


}
