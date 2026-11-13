package demo.mable.game.dto;

import java.util.List;

public record SkillMoveResponse (Long playerId,
                                 int destinationTileIndex,
                                 boolean constructionBuildingActivated,
                                 String itemImagePath,
                                 List<Integer> eligibleTileIndexes) {}
