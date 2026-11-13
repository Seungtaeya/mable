package demo.mable.game.entity;

import demo.mable.game.enums.PlayerStatus;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "game_players")
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "turn_order")
    private Integer turnOrder;
    @Column(name = "player_status")
    @Enumerated(value = EnumType.STRING)
    private PlayerStatus playerStatus;
    @Column(name = "cash")
    private Long cash;
    @Column(name = "position")
    private int position;
    @Column(name = "lap_count")
    private int lapCount;
    @Column(name = "consecutive_dubles")
    private int consecutiveDoubles;
    @Column(name = "island_failed_rolls")
    private int islandFailedRolls;

    public GamePlayer(Integer turnOrder, PlayerStatus playerStatus, Long cash, int position, int lapCount, int consecutive_doubles, int islandFailedRolls) {
        this.turnOrder = turnOrder;
        this.playerStatus = playerStatus;
        this.cash = cash;
        this.position = position;
        this.lapCount = lapCount;
        this.consecutiveDoubles = consecutive_doubles;
        this.islandFailedRolls = islandFailedRolls;
    }

    public void assignGame(Game game) {
        this.game = game;
    }

    public void addUser(User user) {
        this.user = user;
    }
    public void modifyTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }

    public int sumPosition(int diceValue) {
        this.position += diceValue;
        if(position >= 32) {
            position = position - 32;
        }
        return position;
    }

    public void updatePlayerStatus(PlayerStatus status) {
        this.playerStatus = status;
    }

    public void updataDiposit(long cash) {
        this.cash += cash;
    }

    public void payment(Long payment) {
        if(cash >= payment) {
            this.cash = cash - payment;
        }
    }

    public void updateConsecutiveDoubles() {
        this.consecutiveDoubles +=1;
    }
    public void initConsecutiveDoubles() {
        this.consecutiveDoubles = 0;
    }

    public void movePlayerToLand(int position) {
        this.position = position;
    }
}
