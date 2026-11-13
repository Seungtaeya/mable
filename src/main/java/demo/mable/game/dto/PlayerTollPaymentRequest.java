package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerTollPaymentRequest {
    private Long playerId;
    private Integer tileIndex;
}
