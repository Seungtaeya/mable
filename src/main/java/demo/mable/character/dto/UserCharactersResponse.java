package demo.mable.character.dto;

import demo.mable.character.entity.Character;
import demo.mable.character.entity.UserCharacter;
import demo.mable.character.entity.UserSelectedCharacter;
import demo.mable.character.enums.UserCharacterStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserCharactersResponse {

    private Long id;
    private String characterName;
    private String imagePath;
    private int tollDiscount;
    private int bonusGameWin;
    private int specialCostDiscount;
    private int goldenFortuneAcquisition;
    private int constructionCostDiscount;
    private int diceControl;
    private int acquisitionCostDiscount;
    private UserCharacterStatus status;
    private List<SkillResponse> skillResponses;

    public UserCharactersResponse(Long id, String characterName, String imagePath, int tollDiscount, int bonusGameWin, int specialCostDiscount, int goldenFortuneAcquisition, int constructionCostDiscount, int diceControl, int acquisitionCostDiscount,  List<SkillResponse> skillResponses) {
        this.id = id;
        this.characterName = characterName;
        this.imagePath = imagePath;
        this.tollDiscount = tollDiscount;
        this.bonusGameWin = bonusGameWin;
        this.specialCostDiscount = specialCostDiscount;
        this.goldenFortuneAcquisition = goldenFortuneAcquisition;
        this.constructionCostDiscount = constructionCostDiscount;
        this.diceControl = diceControl;
        this.acquisitionCostDiscount = acquisitionCostDiscount;
        this.skillResponses = skillResponses;
    }

    public UserCharactersResponse(Long id, String characterName, String imagePath, int tollDiscount, int bonusGameWin, int specialCostDiscount, int goldenFortuneAcquisition, int constructionCostDiscount, int diceControl, int acquisitionCostDiscount,UserCharacterStatus status) {
        this.id = id;
        this.characterName = characterName;
        this.imagePath = imagePath;
        this.tollDiscount = tollDiscount;
        this.bonusGameWin = bonusGameWin;
        this.specialCostDiscount = specialCostDiscount;
        this.goldenFortuneAcquisition = goldenFortuneAcquisition;
        this.constructionCostDiscount = constructionCostDiscount;
        this.diceControl = diceControl;
        this.acquisitionCostDiscount = acquisitionCostDiscount;
        this.status = status;
    }

    public UserCharactersResponse(Long id, String characterName, String imagePath, int tollDiscount, int bonusGameWin, int specialCostDiscount, int goldenFortuneAcquisition, int constructionCostDiscount, int diceControl, int acquisitionCostDiscount) {
        this.id = id;
        this.characterName = characterName;
        this.imagePath = imagePath;
        this.tollDiscount = tollDiscount;
        this.bonusGameWin = bonusGameWin;
        this.specialCostDiscount = specialCostDiscount;
        this.goldenFortuneAcquisition = goldenFortuneAcquisition;
        this.constructionCostDiscount = constructionCostDiscount;
        this.diceControl = diceControl;
        this.acquisitionCostDiscount = acquisitionCostDiscount;
    }


    public static UserCharactersResponse from(Character ch) {
        return new UserCharactersResponse(
                ch.getId(),
                ch.getCharacterName(),
                ch.getImagePath(),ch.getTollDiscount(),
                ch.getBonusGameWin(),ch.getSpecialCostDiscount(),
                ch.getGoldenFortuneAcquisition(),
                ch.getConstructionCostDiscount(),
                ch.getDiceControl(),
                ch.getAcquisitionCostDiscount());
    }

    public static UserCharactersResponse from(UserCharacter ch) {
        return new UserCharactersResponse(
                ch.getId(),
                ch.getCharacter().getCharacterName(),
                ch.getCharacter().getImagePath(),ch.getCharacter().getTollDiscount(),
                ch.getCharacter().getBonusGameWin(),ch.getCharacter().getSpecialCostDiscount(),
                ch.getCharacter().getGoldenFortuneAcquisition(),
                ch.getCharacter().getConstructionCostDiscount(),
                ch.getCharacter().getDiceControl(),
                ch.getCharacter().getAcquisitionCostDiscount(),
                ch.getStatus());
    }

    public static UserCharactersResponse from(UserSelectedCharacter usc, List<SkillResponse> skills) {
        return new UserCharactersResponse(
                usc.getId(),
                usc.getUserCharacter().getCharacter().getCharacterName(),
                usc.getUserCharacter().getCharacter().getImagePath(),
                usc.getUserCharacter().getCharacter().getTollDiscount(),
                usc.getUserCharacter().getCharacter().getBonusGameWin(),usc.getUserCharacter().getCharacter().getSpecialCostDiscount(),
                usc.getUserCharacter().getCharacter().getGoldenFortuneAcquisition(),
                usc.getUserCharacter().getCharacter().getConstructionCostDiscount(),
                usc.getUserCharacter().getCharacter().getDiceControl(),
                usc.getUserCharacter().getCharacter().getAcquisitionCostDiscount(),
                skills);
    }
}
