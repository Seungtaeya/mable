package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameResultResponse {

    private String userName;
    private int rankNo;
    private Long cash;
    private String finishReason;

    public GameResultResponse(String userName, int rankNo, Long cash, String finishReason) {
        this.userName = userName;
        this.rankNo = rankNo;
        this.cash = cash;
        this.finishReason = finishReason;
    }

    public static GameResultResponse from(String userName, int rankNo, Long cash, String finishReason) {
        return new GameResultResponse(userName, rankNo, cash, finishReason);
    }
}
