package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TurnChangedResponse {
    private Long currentTurnUserId;
    private Long nextPlayerId;
    private String nextAction;
    private List<Integer> eligibleTileIndexes;

    public TurnChangedResponse(Long currentTurnUserId, Long nextPlayerId, String nextAction, List<Integer> eligibleTileIndexes) {
        this.currentTurnUserId = currentTurnUserId;
        this.nextPlayerId = nextPlayerId;
        this.nextAction = nextAction;
        this.eligibleTileIndexes = eligibleTileIndexes;
    }

    public TurnChangedResponse(Long currentTurnUserId, Long nextPlayerId) {
        this.currentTurnUserId = currentTurnUserId;
        this.nextPlayerId = nextPlayerId;
    }

    public TurnChangedResponse(Long currentTurnUserId, Long nextPlayerId, String nextAction) {

        this.currentTurnUserId = currentTurnUserId;
        this.nextPlayerId = nextPlayerId;
        this.nextAction = nextAction;
    }

    public static TurnChangedResponse from(Long currentTurnUserId, Long nextPlayerId) {
        return new TurnChangedResponse(currentTurnUserId, nextPlayerId);
    }

    public static TurnChangedResponse fromTrainTravel(Long currentTurnUserId, Long nextPlayerId, String nextAction, List<Integer> eligibleTileIndexes) {
        return new TurnChangedResponse(currentTurnUserId, nextPlayerId, nextAction, eligibleTileIndexes);
    }

    public static TurnChangedResponse fromRandomDestination(Long currentTurnUserId, Long nextPlayerId, String nextAction) {
        return new TurnChangedResponse(currentTurnUserId, nextPlayerId, nextAction);
    }
}
