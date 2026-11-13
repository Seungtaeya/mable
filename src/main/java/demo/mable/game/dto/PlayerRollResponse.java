package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlayerRollResponse {

    private Long gameId;
    private Long playerId;
    private int destinationTileIndex;
    private Boolean existsTile;
    int dice1 = 0;
    int dice2 = 0;
    private boolean diceControlActivated;
    private List<String> activatedItemImagePath;
    private String landingAction;
    private Long playerCash;

    public PlayerRollResponse(Long gameId, Long playerId, int destinationTileIndex, Boolean existsTile, int dice1, int dice2, boolean diceControlActivated, List<String> activatedItemImagePath) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.destinationTileIndex = destinationTileIndex;
        this.existsTile = existsTile;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.diceControlActivated = diceControlActivated;
        this.activatedItemImagePath = activatedItemImagePath;
    }

    public PlayerRollResponse(Long gameId, Long playerId, int destinationTileIndex, Boolean existsTile) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.destinationTileIndex = destinationTileIndex;
        this.existsTile = existsTile;
    }

    public static PlayerRollResponse from(Long gameId, Long playerId, int destinationTileIndex, Boolean existsTile, int dice1, int dice2, boolean diceControlActivated, List<String> activatedItemImagePath) {
        return new PlayerRollResponse(gameId, playerId, destinationTileIndex,existsTile, dice1, dice2, diceControlActivated, activatedItemImagePath);
    }

    public static PlayerRollResponse forTrainTravel(Long gameId, Long playerId, int destinationTileIndex,Boolean existsTile) {
        return new PlayerRollResponse(gameId, playerId, destinationTileIndex, existsTile);
    }

}
