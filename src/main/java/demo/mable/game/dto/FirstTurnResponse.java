package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FirstTurnResponse {
    private Integer firstCardIndex;
    private Long currentTurnUserId;
    private Long playerId;
    private String pieceImage = "/uploads/characters/piece-Image/mixpsyche.png";
    public FirstTurnResponse(Integer firstCardIndex, Long currentTurnUserId, Long playerId) {
        this.firstCardIndex = firstCardIndex;
        this.currentTurnUserId = currentTurnUserId;
        this.playerId = playerId;
    }

    public static FirstTurnResponse from(int position, Long UserId, Long playerId) {
        return new FirstTurnResponse(position,UserId, playerId);
    }
}
