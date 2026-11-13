package demo.mable.game.dto;

import demo.mable.game.enums.GroupCode;
import demo.mable.game.enums.TileType;

public record BoardTileDefinition(int pathIndex,
                                  String name,
                                  TileType type,
                                  GroupCode groupCode,
                                  long landPrice) {
}
