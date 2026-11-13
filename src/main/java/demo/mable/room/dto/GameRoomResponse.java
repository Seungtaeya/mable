package demo.mable.room.dto;

import demo.mable.room.entity.GameRoom;
import demo.mable.room.enums.GameRoomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GameRoomResponse {
    private Long roomId;
    private String title;
    private String hostName;
    private GameRoomStatus status;
    private int currentPlayerCount;
    private int maxPlayers;
    private LocalDateTime createdAt;

    public GameRoomResponse(Long roomId, String title, String hostId, GameRoomStatus status, int currentPlayerCount, int maxPlayers, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.title = title;
        this.hostName = hostId;
        this.status = status;
        this.currentPlayerCount = currentPlayerCount;
        this.maxPlayers = maxPlayers;
        this.createdAt = createdAt;
    }

    public static GameRoomResponse from(GameRoom gameRoom ) {
        return new GameRoomResponse(
                gameRoom.getId(),
                gameRoom.getTitle(),
                gameRoom.getHostUser().getUsername(),
                gameRoom.getStatus(),
                gameRoom.getPlayerCount(),
                gameRoom.getMaxPlayers(),
                gameRoom.getCreated()
        );
    }


}
