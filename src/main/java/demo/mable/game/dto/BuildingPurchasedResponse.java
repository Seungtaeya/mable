package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BuildingPurchasedResponse {

    private Long playerId;
    private Long playerCash;
    private int tileId;
    private boolean has_flag;
    private boolean has_house;
    private boolean has_villa;
    private boolean has_apartment;
    private boolean has_landmark;

    public BuildingPurchasedResponse(Long playerId, Long playerCash, int tileId, boolean has_flag, boolean has_house, boolean has_villa, boolean has_apartment, boolean has_landmark) {
        this.playerId = playerId;
        this.playerCash = playerCash;
        this.tileId = tileId;
        this.has_flag = has_flag;
        this.has_house = has_house;
        this.has_villa = has_villa;
        this.has_apartment = has_apartment;
        this.has_landmark = has_landmark;
    }

    public static BuildingPurchasedResponse from(Long playerId, Long playerCash,int tileId, boolean has_flag, boolean has_house, boolean has_villa, boolean has_apartment, boolean has_landmark) {
        return new BuildingPurchasedResponse(playerId,playerCash,tileId, has_flag, has_house, has_villa, has_apartment, has_landmark);
    }
}
