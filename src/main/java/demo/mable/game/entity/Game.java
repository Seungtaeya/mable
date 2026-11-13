package demo.mable.game.entity;

import demo.mable.game.enums.GamePhase;
import demo.mable.game.enums.GameStatus;
import demo.mable.game.enums.MapCode;
import demo.mable.room.entity.GameRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private GameRoom gameRoom;
    @Column(name = "map_code")
    @Enumerated(value = EnumType.STRING)
    private MapCode mapCode;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private GameStatus status;
    @Column(name = "phase")
    @Enumerated(value = EnumType.STRING)
    private GamePhase phase;
    @Column(name = "current_player_id")
    private Long currentPlayerId;
    @Column(name = "action_deadline")
    private LocalDateTime actionDeadline;
    @Column(name = "finish_reason")
    private String finishReason;
    @Column(name = "version")
    @Version
    private Long version;
    @Column(name = "started_at")
    private LocalDateTime started;
    @Column(name = "ended_at")
    private LocalDateTime ended;

    public Game(MapCode mapCode, GameStatus status, GamePhase phase, int round_no, Long currentPlayerId, LocalDateTime actionDeadline) {
        this.mapCode = mapCode;
        this.status = status;
        this.phase = phase;
        this.currentPlayerId = currentPlayerId;
        this.actionDeadline = actionDeadline;
    }

    @PrePersist
    private void startGame() {
        this.started = LocalDateTime.now();
    }

    public void changePhase(GamePhase phase) {
        this.phase = phase;
    }
    public void finishGame() {
        this.ended = LocalDateTime.now();
    }

    public void assignRoom(GameRoom room) {
        this.gameRoom = room;
    }
    public void modifyCurrentPlayerId(Long playerId) {
        this.currentPlayerId = playerId;
    }
    public void updateGameStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }
    public void updateFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
    public void ended() {
        this.ended = LocalDateTime.now();
    }
}
