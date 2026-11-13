package demo.mable.admin.service;

import demo.mable.admin.enums.AssetType;
import demo.mable.character.dto.CharacterRequest;
import demo.mable.character.entity.Character;
import demo.mable.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AdminCharacterService {

    private final CharacterRepository characterRepository;
    private final ImageStorageService imageStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createCharacter(MultipartFile image, CharacterRequest requestCharacter) throws IOException {
        String imageUrl = imageStorageService.save(image, AssetType.CHARACTER);

        if(requestCharacter == null) {
            throw new IllegalArgumentException("캐릭터 정보는 필수입니다.");
        }
        Character character = new Character(requestCharacter.characterName(),
                imageUrl,
                requestCharacter.tollDiscount(),
                requestCharacter.bonusGameWin(),
                requestCharacter.specialCostDisCount(),
                requestCharacter.goldenFortuneAcquisition(),
                requestCharacter.constructCostDisCount(),
                requestCharacter.diceControl(),
                requestCharacter.acquisitionCostDiscount(),
                requestCharacter.rank());


        characterRepository.save(character);
    }
}
