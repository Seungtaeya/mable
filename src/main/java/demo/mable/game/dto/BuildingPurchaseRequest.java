package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BuildingPurchaseRequest {

    private Integer tileId;
    private Long playerId;
    private List<Integer> buildingLevels;
}
