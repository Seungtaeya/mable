package demo.mable.character.entity;

import demo.mable.skill.entity.Skill;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UserSelectedCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_character_id", nullable = false)
    private UserCharacter userCharacter;

    @Column(name = "updated_at")
    private LocalDateTime update;

    public void assignUser(User user) {
        this.user = user;
    }
    public void assignCharacter(UserCharacter character) {
        this.userCharacter = character;
    }

    public void changeCharacter(UserCharacter character) {
        this.userCharacter = character;
    }

    public void updateTime() {
        this.update = LocalDateTime.now();
    }
}
