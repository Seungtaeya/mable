package demo.mable.game.entity;

import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "game_results")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "rank_no")
    private int rank_no;
    @Column(name = "is_winner")
    private boolean winner;
    @Column(name = "cash")
    private Long cash;
    @Column(name = "finish_reason")
    private String finishReason;
    @Column(name = "created_at")
    private LocalDateTime created;

    @PrePersist
    private void created() {
        this.created = LocalDateTime.now();
    }

    public GameResult(int rank_no, boolean winner, Long cash, String finishReason) {
        this.rank_no = rank_no;
        this.winner = winner;
        this.cash = cash;
        this.finishReason = finishReason;
    }

    public void assignGame(Game game) {
        this.game = game;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
