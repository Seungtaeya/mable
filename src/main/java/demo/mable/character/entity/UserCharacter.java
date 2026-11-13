package demo.mable.character.entity;

import demo.mable.character.enums.UserCharacterStatus;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_characters")
public class UserCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Column(name = "character_status")
    @Enumerated(EnumType.STRING)
    private UserCharacterStatus status = UserCharacterStatus.ACTIVE;

    public void statusChange(UserCharacterStatus userCharacterStatus) {
        this.status = userCharacterStatus;
    }
    public void assignUser(User user) {
        this.user = user;
    }
    public void assignCharacter(Character character) {
        this.character = character;
    }
}
