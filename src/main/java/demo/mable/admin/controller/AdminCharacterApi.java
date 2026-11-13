package demo.mable.admin.controller;

import demo.mable.admin.service.AdminCharacterService;
import demo.mable.character.dto.CharacterRequest;
import demo.mable.luckyItem.dto.CreateLuckyItemRequest;
import demo.mable.luckyItem.service.LuckItemService;
import demo.mable.skill.dto.CreateCharacterSkillRequest;
import demo.mable.skill.service.CharacterSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AdminCharacterApi {

    private final AdminCharacterService adminCharacterService;
    private final CharacterSkillService characterSkillService;
    private final LuckItemService luckItemService;

    @GetMapping("/api/test")
    public void test(Authentication authentication)  {
        System.out.println("authentication.getName() = " + authentication.getName());
        System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());
    }

    @PostMapping("/api/admin/character")
    public ResponseEntity<Void> createCharacter(@RequestPart("image") MultipartFile image,
                                          @RequestPart("data") CharacterRequest character) throws IOException {
        adminCharacterService.createCharacter(image, character);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/admin/character-skill")
    public ResponseEntity<Void> createCharacterSkill( @RequestPart("data") @Valid CreateCharacterSkillRequest request, @RequestPart("image")MultipartFile image) throws IOException {
        characterSkillService.createCharacterSkill(request,image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/admin/luckyItem")
    public ResponseEntity<Void> createLuckyItem(@RequestPart("data") @Valid CreateLuckyItemRequest request, @RequestPart("image") MultipartFile image) throws IOException {
        luckItemService.createLuckyItem(request,image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
