package demo.mable.game.controller;


import demo.mable.game.dto.BoardTileDefinition;
import demo.mable.game.dto.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardMapController {

   @GetMapping("/maps")
    public List<BoardTileDefinition> getMap() {
       return Map.TILES;
   }
}
