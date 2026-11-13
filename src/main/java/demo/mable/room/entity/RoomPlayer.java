package demo.mable.room.entity;

import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_players")
@NoArgsConstructor
@Getter
public class RoomPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private GameRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "seat_order")
    private int gameOrder;

    @Column(name = "ready")
    private boolean gameReady = false;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "exited_at")
    private LocalDateTime exitedAt;

    @PrePersist
    private void setJoinedAt() {
        this.joinedAt = LocalDateTime.now();
    }

    public void toggleReady() {
        this.gameReady = !gameReady;
    }

    public void addUser(User user) {
        this.user = user;
    }
    public void modifyGameOrder(int turnOrder) {
        this.gameOrder = turnOrder;
    }

    public void assignRoom(GameRoom gameRoom) {
        this.room = gameRoom;
    }


}
