package demo.mable.game.dto;

import demo.mable.game.entity.GamePlayer;
import demo.mable.game.enums.PlayerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GamePlayerResponse {

    private Long playerId;
    private String userName;
    private Integer turnOrder;
    private String characterImage;
    private Long cash;
    private PlayerStatus playerStatus;
    private int position;
    private int lapCount;
    private int consecutiveDoubles;
    private int islandFailedRolls;
    private String pieceImage;

    public static GamePlayerResponse from(GamePlayer gp) {
        return new GamePlayerResponse(gp.getUser().getUsername(),
                gp.getTurnOrder(),
                gp.getPlayerStatus(),
                gp.getCash(),
                gp.getPosition(),
                gp.getLapCount(),
                gp.getConsecutiveDoubles(),
                gp.getIslandFailedRolls());
    }

    public GamePlayerResponse(Long playerId, String userName, Integer turnOrder, Long cash, String characterImage, String pieceImage) {
        this.playerId = playerId;
        this.userName = userName;
        this.turnOrder = turnOrder;
        this.characterImage = characterImage;
        this.cash = cash;
        this.pieceImage = pieceImage;
    }

    public GamePlayerResponse(String userName, Integer turnOrder, PlayerStatus playerStatus, Long cash, int position, int lapCount, int consecutiveDoubles, int islandFailedRolls) {
        this.userName = userName;
        this.turnOrder = turnOrder;
        this.playerStatus = playerStatus;
        this.cash = cash;
        this.position = position;
        this.lapCount = lapCount;
        this.consecutiveDoubles = consecutiveDoubles;
        this.islandFailedRolls = islandFailedRolls;
    }
}
