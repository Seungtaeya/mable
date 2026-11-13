package demo.mable.game.dto;

import demo.mable.game.enums.BuildingType;

import java.util.List;

public record UnbuiltBuildingTypesResponse (
    Integer tileId,
    List<BuildingType> unbuiltBuildingTypes
){ }
