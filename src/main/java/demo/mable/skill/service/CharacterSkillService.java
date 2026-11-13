package demo.mable.skill.service;

import demo.mable.admin.enums.AssetType;
import demo.mable.admin.service.ImageStorageService;
import demo.mable.character.entity.Character;
import demo.mable.character.exception.CharacterNofFoundException;
import demo.mable.character.repository.CharacterRepository;
import demo.mable.skill.dto.CreateCharacterSkillRequest;
import demo.mable.skill.entity.CharacterSkill;
import demo.mable.skill.entity.Skill;
import demo.mable.skill.repository.CharacterSkillRepo;
import demo.mable.skill.repository.SkillRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterSkillService {

    private final SkillRepo skillRepo;
    private final CharacterSkillRepo characterSkillRepo;
    private final CharacterRepository characterRepository;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = false)
    public void createCharacterSkill(CreateCharacterSkillRequest request, MultipartFile image) throws IOException {
        if(request == null) {
            throw new IllegalArgumentException("request is null");
        }

        String saveImage = imageStorageService.save(image, AssetType.CHARACTER_SKILL);

        Character character = characterRepository.findById(request.getCharacterId()).orElseThrow(CharacterNofFoundException::new);
        Skill skill = new Skill(request.getSkillCode(), request.getTriggerType(), request.getSkillName(), request.getSkillDescription(),saveImage);

        Skill saveSkill = skillRepo.save(skill);

        CharacterSkill characterSkill = new CharacterSkill(request.getActivationRate(), request.getEffectValue(), request.getDisplayOrder());
        characterSkill.assignSkill(saveSkill);
        characterSkill.assignCharacter(character);
        characterSkillRepo.save(characterSkill);
    }


}
