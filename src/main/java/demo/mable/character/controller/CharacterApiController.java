package demo.mable.character.controller;

import demo.mable.character.dto.*;
import demo.mable.character.service.CharacterService;
import demo.mable.character.service.UserCharacterService;
import demo.mable.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CharacterApiController {

    private final UserCharacterService userCharacterService;
    private final CharacterService characterService;

    @GetMapping("/character-cards")
    public ResponseEntity<List<UserCharactersResponse>> getCharacterCards(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<UserCharactersResponse> characters = userCharacterService.getCharactersByUserId(customUserDetails.getUserId());

        return ResponseEntity.ok().body(characters);
    }

    @GetMapping("/character-info/{characterId}")
    public ResponseEntity<UserCharactersResponse> getCharacter(@PathVariable Long characterId) {
        UserCharactersResponse character = characterService.getCharacter(characterId);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/users/me/character")
    public ResponseEntity<UserCharactersResponse> getSelectedMyCharacter(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserCharactersResponse uc = userCharacterService.findSelectedCharacterByUserId(userDetails.getUserId());
        return ResponseEntity.ok().body(uc);
    }

    @GetMapping("/users/me/selected-character-card")
    public ResponseEntity<CharacterCardForLobbyResponse> getCharacterCardForLobby(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CharacterCardForLobbyResponse selectedCharacterCard = userCharacterService.findSelectedCharacterCardByUserId(userDetails.getUserId());
        return ResponseEntity.ok(selectedCharacterCard);
    }

    @PostMapping("/users/me/selected-character")
    public ResponseEntity<Void> selectCharacter(@RequestBody CharacterSelectRequest select, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userCharacterService.findExistsByUserId(userDetails.getUserId())){
            userCharacterService.selectCharacter(userDetails.getUserId(), select.characterId());
        } else  {
            System.out.println("select.characterId() = " + select.characterId());
            userCharacterService.selectCharacterChange(userDetails.getUserId(), select.characterId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users/me/draw-cards")
    public ResponseEntity<List<DrawnCardResponse>> drawCards(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody String cardPack) {
        List<DrawnCardResponse> drawnCards = userCharacterService.drawCards(customUserDetails.getUserId(), cardPack);
        return ResponseEntity.ok(drawnCards);
    }

    @PostMapping("/users/me/characters/synthesize")
    public ResponseEntity<CharacterSynthesisResponse> synthesizeCharacter(@RequestBody CharacterSynthesisRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        CharacterSynthesisResponse characterSynthesisResponse = userCharacterService.synthesizeCharacter(userDetails.getUserId(), request);
        return ResponseEntity.ok(characterSynthesisResponse);
    }
}
