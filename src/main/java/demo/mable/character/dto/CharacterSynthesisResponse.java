package demo.mable.character.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CharacterSynthesisResponse {
    private String imagePath;

    public CharacterSynthesisResponse(String imagePath) {
        this.imagePath = imagePath;
    }
}
