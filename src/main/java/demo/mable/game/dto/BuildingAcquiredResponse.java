package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuildingAcquiredResponse {

    private Integer tileId;
    private Long previousOwnerId;
    private Long previousOwnerCash;
    private Long newOwnerId;
    private Long newOwnerCash;

    public BuildingAcquiredResponse(Integer tileId, Long previousOwnerId, Long previousOwnerCash, Long newOwnerId, Long newOwnerCash) {
        this.tileId = tileId;
        this.previousOwnerId = previousOwnerId;
        this.previousOwnerCash = previousOwnerCash;
        this.newOwnerId = newOwnerId;
        this.newOwnerCash = newOwnerCash;
    }

    public static BuildingAcquiredResponse from(Integer tileId, Long previousOwnerId, Long previousOwnerCash, Long newOwnerId, Long newOwnerCash) {
        return new BuildingAcquiredResponse(tileId, previousOwnerId, previousOwnerCash, newOwnerId, newOwnerCash);
    }
}
