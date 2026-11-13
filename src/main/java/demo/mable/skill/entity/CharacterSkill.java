package demo.mable.skill.entity;

import demo.mable.character.entity.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "character_skills")
public class CharacterSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "activation_rate")
    private int activationRate;
    @Column(name = "effect_value")
    private int effectValue;
    @Column(name = "display_order")
    private int displayOrder;

    public CharacterSkill(int activationRate, int effectValue, int displayOrder) {
        this.activationRate = activationRate;
        this.effectValue = effectValue;
        this.displayOrder = displayOrder;
    }

    public void assignCharacter(Character character) {
        this.character = character;
    }

    public void assignSkill(Skill skill) {
        this.skill = skill;
    }
}
