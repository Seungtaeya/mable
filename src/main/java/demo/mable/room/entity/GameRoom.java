package demo.mable.room.entity;

import demo.mable.room.enums.GameRoomStatus;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "game_rooms")
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", length = 10)
    private String roomCode;
    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private User hostUser;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private GameRoomStatus status;

    @Column(name = "player_count")
    private int playerCount;

    @Column(name = "max_players")
    private int maxPlayers;

    @Column(name = "created_at")
    private LocalDateTime created;
    @Column(name = "closed_at")
    private LocalDateTime closed;

    @PrePersist
    private void onCreate(){
        this.created = LocalDateTime.now();
    }

    public boolean isHost(Long userId) {
        return this.hostUser.getId().equals(userId);
    }

    public void closeRoom() {
        if(this.playerCount != 0) {
            throw new IllegalStateException("참가자가 남아 있는 방은 닫을 수 없습니다.");
        }
        this.closed = LocalDateTime.now();
        this.status = GameRoomStatus.CLOSED;
    }

    public void changeStatus(GameRoomStatus status) {
        this.status = status;
    }
    public void changeHostUser(User user) {
        this.hostUser = user;
    }
    public void addPlayerCount() {
        if(playerCount < maxPlayers) {
            this.playerCount += 1;
        }
    }

    public void decrementPlayerCount() {
        if(playerCount > 0) {
            this.playerCount -= 1;
        }
    }
    public void createRoom(String title, User hostUser, int maxPlayers) {
        this.title = title;
        this.hostUser = hostUser;
        this.maxPlayers = maxPlayers;
        this.playerCount +=1;
        this.status = GameRoomStatus.WAITING;
    }
}
