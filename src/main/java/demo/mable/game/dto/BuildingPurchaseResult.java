package demo.mable.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record BuildingPurchaseResult (BuildingPurchasedResponse building, TurnChangedResponse nextRoll,SkillMoveResponse skillMove) {}
