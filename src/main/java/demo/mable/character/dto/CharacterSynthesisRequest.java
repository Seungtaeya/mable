package demo.mable.character.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CharacterSynthesisRequest {
    private Long leftCharacterId;
    private Long rightCharacterId;
}
