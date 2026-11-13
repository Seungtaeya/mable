package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerSelectTurnOrderResponse {
    private Long playerId;
    private int selectTurnOrder;

    public PlayerSelectTurnOrderResponse(Long playerId, int selectTurnOrder) {
        this.playerId = playerId;
        this.selectTurnOrder = selectTurnOrder;
    }
}
