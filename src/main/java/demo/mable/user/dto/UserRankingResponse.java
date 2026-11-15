package demo.mable.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRankingResponse {
    private Integer ranking;
    private String userName;
    private Long victoryCount;

    public UserRankingResponse(String userName, Long victoryCount) {
        this.userName = userName;
        this.victoryCount = victoryCount;
    }
}
