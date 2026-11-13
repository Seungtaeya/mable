package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerMoveResponse {

    private Long gameId;
    private Long playerId;
    private Integer destinationTileIndex;
    private boolean activated;
    private String itemImagePath;

    public PlayerMoveResponse(Long gameId, Long playerId, Integer destinationTileIndex, boolean activated, String itemImagePath) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.destinationTileIndex = destinationTileIndex;
        this.activated = activated;
        this.itemImagePath = itemImagePath;
    }

    public static PlayerMoveResponse from(Long gameId, Long playerId, Integer destinationTileIndex, boolean activated, String itemImagePath) {
        return new PlayerMoveResponse(gameId, playerId, destinationTileIndex, activated, itemImagePath);
    }
}
