package demo.mable.character.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DrawnCardResponse {

    private String characterName;
    private String imagePath;
    private String rank;

    public DrawnCardResponse(String characterName, String imagePath, String rank) {
        this.characterName = characterName;
        this.imagePath = imagePath;
        this.rank = rank;
    }

    public static DrawnCardResponse from(String characterName, String imagePath, String rank) {
        return new DrawnCardResponse(characterName, imagePath, rank);
    }
}
