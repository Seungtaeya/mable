package demo.mable.game.service;

import demo.mable.game.dto.UnbuiltBuildingTypesResponse;
import demo.mable.game.entity.GameProperties;
import demo.mable.game.enums.BuildingType;
import demo.mable.game.repository.GamePropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TileService {

    private final GamePropertiesRepository propertiesRepository;


    public long getTileConstructionCost(Long gameId, Integer tileId) {
        GameProperties tile = propertiesRepository.findByGameIdAndTileId(gameId, tileId);
        return tile.getConstructionCost();
    }

    public UnbuiltBuildingTypesResponse findUnbuiltBuildingTypes(Long gameId, Integer tileId) {
        GameProperties tile = propertiesRepository.findByGameIdAndTileId(gameId, tileId);
        List<BuildingType> unbuiltBuildingTypes = new ArrayList<>();

        if(tile.isHouse() && tile.isVilla() && tile.isApartment()) {
            if(!tile.isLandmark()) {
                unbuiltBuildingTypes.add(BuildingType.LANDMARK);
            }
        }
        if(!tile.isVilla()) {
            unbuiltBuildingTypes.add(BuildingType.VILLA);
        }
        if(!tile.isApartment()) {
            unbuiltBuildingTypes.add(BuildingType.APARTMENT);
        }
        if(!tile.isHouse()) {
            unbuiltBuildingTypes.add(BuildingType.HOUSE);
        }
        return new UnbuiltBuildingTypesResponse(tileId, unbuiltBuildingTypes);
    }
}
