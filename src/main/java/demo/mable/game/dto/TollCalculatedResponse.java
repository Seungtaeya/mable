package demo.mable.game.dto;

import demo.mable.game.enums.PlayerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TollCalculatedResponse {

    private int tileId;
    private Long tileOwnerId;
    private Long tileOwnerCash;
    private Long tollPayerId;
    private Long tollPayerCash;
    private PlayerStatus tollPlayerStatus;
    private boolean isLandmark;

    public TollCalculatedResponse(int tileId,Long tileOwnerId, Long tileOwnerCash, Long tollPayerId, Long tollPayerCash, PlayerStatus tollPlayerStatus,  boolean isLandmark) {
        this.tileId = tileId;
        this.tileOwnerId = tileOwnerId;
        this.tileOwnerCash = tileOwnerCash;
        this.tollPayerId = tollPayerId;
        this.tollPayerCash = tollPayerCash;
        this.tollPlayerStatus = tollPlayerStatus;
        this.isLandmark = isLandmark;
    }

    public TollCalculatedResponse(int tileId,Long tileOwnerId, Long tileOwnerCash, Long tollPayerId, Long tollPayerCash) {
        this.tileId = tileId;
        this.tileOwnerId = tileOwnerId;
        this.tileOwnerCash = tileOwnerCash;
        this.tollPayerId = tollPayerId;
        this.tollPayerCash = tollPayerCash;
    }

    public static TollCalculatedResponse from(int tileId,Long tileOwnerId, Long tileOwnerCash, Long tollPayerId, Long tollPayerCash, PlayerStatus tollPlayerStatus, boolean isLandmark) {
        if(tollPlayerStatus == PlayerStatus.BANKRUPT) {
            return new TollCalculatedResponse(tileId,tileOwnerId, tileOwnerCash, tollPayerId, tollPayerCash);
        }
        return new TollCalculatedResponse(tileId,tileOwnerId, tileOwnerCash, tollPayerId, tollPayerCash, tollPlayerStatus, isLandmark);
    }
}
