package demo.mable.game.controller;

import demo.mable.character.dto.UserCharactersResponse;
import demo.mable.character.service.UserCharacterService;
import demo.mable.game.dto.BoardTileDefinition;
import demo.mable.game.dto.GamePlayerResponse;
import demo.mable.game.dto.Map;
import demo.mable.game.dto.UnbuiltBuildingTypesResponse;
import demo.mable.game.entity.GamePlayer;
import demo.mable.game.enums.BuildingType;
import demo.mable.game.enums.TileType;
import demo.mable.game.exception.TileNotFoundException;
import demo.mable.game.service.GamePlayerService;
import demo.mable.game.service.TileService;
import demo.mable.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final UserCharacterService userCharacterService;
    private final GamePlayerService gamePlayerService;
    private final TileService tileService;

    @GetMapping("/games/{gameId}")
    public String map(@PathVariable Long gameId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("gameId", gameId);
        model.addAttribute("userId", userDetails.getUserId());
        return "game/game";
    }

    @GetMapping("/games/{gameId}/{playerId}/tiles/{tileId}/construction")
    public String showConstructionWindow(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable int tileId, Model model,@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BoardTileDefinition> tiles = Map.TILES;

        BoardTileDefinition currentTile = tiles.stream().filter(tile -> tile.pathIndex() == tileId).findFirst().orElseThrow(TileNotFoundException::new);
        UserCharactersResponse selectedCharacter = userCharacterService.findSelectedCharacterByUserId(userDetails.getUserId());
        int constructionCost = (int) ((currentTile.landPrice()) + (currentTile.landPrice() * 0.4) + (currentTile.landPrice() * 0.6)  + (currentTile.landPrice()));
        GamePlayerResponse playerById = gamePlayerService.getPlayerById(playerId);

        if (currentTile.type().equals(TileType.TOURIST_SPOT)) {
            model.addAttribute("playerCash",playerById.getCash());
            model.addAttribute("gameId", gameId);
            model.addAttribute("playerId", playerId);
            model.addAttribute("tileId", tileId);
            model.addAttribute("landPrice",currentTile.landPrice());
        } else {
            model.addAttribute("playerCash", playerById.getCash());
            model.addAttribute("gameId", gameId);
            model.addAttribute("playerId", playerId);
            model.addAttribute("tileId", tileId);
            model.addAttribute("landPrice", currentTile.landPrice());
            model.addAttribute("housePrice", currentTile.landPrice() * 0.4);
            model.addAttribute("villaPrice", currentTile.landPrice() * 0.6);
            model.addAttribute("apartmentPrice", currentTile.landPrice());
            model.addAttribute("constructionCost", constructionCost);
            model.addAttribute("constructionCostDiscount", selectedCharacter.getConstructionCostDiscount());
        }
        return "game/ConstructionBulidingForm";
    }

    @GetMapping("/games/{gameId}/tiles/{tileId}/acquisition")
    public String  acquisitionForm(@PathVariable Long gameId, @PathVariable Integer tileId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model)  {
        List<BoardTileDefinition> tiles = Map.TILES;

        Long playerId = gamePlayerService.validatePlayerIsOnTile(gameId, tileId, userDetails.getUsername());
        long tileConstructionCost = tileService.getTileConstructionCost(gameId, tileId);
        UserCharactersResponse selectedCharacter = userCharacterService.findSelectedCharacterByUserId(userDetails.getUserId());

        BoardTileDefinition boardTileDefinition = tiles.stream().filter(tile -> tile.pathIndex() == tileId).findFirst().orElseThrow(TileNotFoundException::new);
        long constructionCostDiscount = (long) ((tileConstructionCost * 2) * (100 - selectedCharacter.getAcquisitionCostDiscount()) / 100.0);
        model.addAttribute("gameId", gameId);
        model.addAttribute("tileId", tileId);
        model.addAttribute("tileName", boardTileDefinition.name());
        model.addAttribute("playerId", playerId);
        model.addAttribute("tileConstructionCost",tileConstructionCost * 2);
        model.addAttribute("constructionCostDiscount", constructionCostDiscount);
        return "game/acquisitionForm";
    }

    @GetMapping("/games/{gameId}/players/{playerId}/tiles/{tileId}/skill-construction")
    public String skillConstructionForm(@PathVariable Long gameId, @PathVariable Long playerId, @PathVariable Integer tileId, Model model) {
        UnbuiltBuildingTypesResponse unbuiltBuildingTypes = tileService.findUnbuiltBuildingTypes(gameId, tileId);

        model.addAttribute("gameId", gameId);
        model.addAttribute("tileId", tileId);
        model.addAttribute("playerId", playerId);
        for (BuildingType unbuiltBuildingType : unbuiltBuildingTypes.unbuiltBuildingTypes()) {
            model.addAttribute(unbuiltBuildingType.name() ,unbuiltBuildingType.name());
        }

        return "game/skill-construction";
    }

    @GetMapping("/games/{gameId}/tiles/{tileId}/upgrade-construction")
    public String skillConstructionForm(@PathVariable Long gameId,@PathVariable Integer tileId, Model model) {
        UnbuiltBuildingTypesResponse result = tileService.findUnbuiltBuildingTypes(gameId, tileId);

        model.addAttribute("gameId", gameId);
        model.addAttribute("tileId", tileId);
        model.addAttribute("LANDMARK", BuildingType.LANDMARK);
        return "game/skill-construction";
    }
}
