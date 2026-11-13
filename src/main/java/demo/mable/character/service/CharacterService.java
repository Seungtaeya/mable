package demo.mable.character.service;

import demo.mable.character.dto.UserCharactersResponse;
import demo.mable.character.entity.Character;
import demo.mable.character.entity.UserCharacter;
import demo.mable.character.repository.CharacterRepository;
import demo.mable.character.repository.UserCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final UserCharacterRepository userCharacterRepository;
    public UserCharactersResponse getCharacter(Long characterId) {

        if(characterId == null) {
            throw new IllegalArgumentException("캐릭터 번호는 필수입니다.");
        }
        UserCharacter userCharacter = userCharacterRepository.findById(characterId).orElseThrow(() -> new IllegalArgumentException("등록된 캐릭터가 아닙니다. 다시 확인해주세요"));

        return UserCharactersResponse.from(userCharacter);
    }

    public List<UserCharactersResponse> getSPlusCharacters() {
        List<Character> all = characterRepository.findAllByRank("S+");

        return all.stream().map(UserCharactersResponse::from).toList();
    }
}
