package demo.mable.character.dto;

public record CharacterRequest(String characterName,
                               int tollDiscount,
                               int bonusGameWin,
                               int specialCostDisCount,
                               int goldenFortuneAcquisition,
                               int constructCostDisCount,
                               int diceControl,
                               int acquisitionCostDiscount,
                               String rank) {}
