package demo.mable.game.dto;

import demo.mable.character.dto.SkillResponse;
import demo.mable.luckyItem.dto.EquipItemResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GamePlayerDetailResponse {
    private Long userId;
    private Long characterId;
    private String characterName;
    private String characterImagePath;
    private List<SkillResponse> characterSkills;
    private List<EquipItemResponse> equipItems;

    public GamePlayerDetailResponse(Long userId, Long characterId, String characterName, String characterImagePath) {
        this.userId = userId;
        this.characterId = characterId;
        this.characterName = characterName;
        this.characterImagePath = characterImagePath;
    }


}
