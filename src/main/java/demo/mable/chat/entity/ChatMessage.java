package demo.mable.chat.entity;

import demo.mable.chat.enums.MessageType;
import demo.mable.game.entity.Game;
import demo.mable.room.entity.GameRoom;
import demo.mable.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private GameRoom gameRoom;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "game_id")
//    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User sender;

    @Column(name = "message_type")
    @Enumerated(value = EnumType.STRING)
    private MessageType messageType;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ChatMessage(User sender, MessageType messageType, String content) {
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
    }

    public ChatMessage(GameRoom gameRoom, User sender, MessageType messageType, String content) {
        this.gameRoom = gameRoom;
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
    }

    @PrePersist
    private void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    protected void deleteTime() {
        this.deletedAt = LocalDateTime.now();
    }
}
