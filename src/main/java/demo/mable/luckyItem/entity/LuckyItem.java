package demo.mable.luckyItem.entity;

import demo.mable.skill.entity.Skill;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "lucky_items")
public class LuckyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "item_name")
    private String itemName;
    @Column(name = "item_rank")
    private String itemRank;
    @Column(name = "activation_rate")
    private Integer activationRate;
    @Column(name = "effect_value")
    private Integer effectValue;

    public LuckyItem(String itemName, String itemRank, Integer activationRate) {
        this.itemName = itemName;
        this.itemRank = itemRank;
        this.activationRate = activationRate;
    }

    public void assignSkill(Skill skill) {
        this.skill = skill;
    }
}
