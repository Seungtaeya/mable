package demo.mable.game.entity;

import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "game_turnorder")
public class TurnOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "turn")
    private int turn;

    public TurnOrder(int turn) {
        this.turn = turn;
    }

    public void assignGame(Game game) {
        this.game = game;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
