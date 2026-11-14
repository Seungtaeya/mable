package demo.mable.character.service;

import demo.mable.character.dto.*;
import demo.mable.character.entity.Character;
import demo.mable.character.entity.UserCharacter;
import demo.mable.character.entity.UserSelectedCharacter;
import demo.mable.character.enums.UserCharacterStatus;
import demo.mable.character.exception.*;
import demo.mable.character.repository.CharacterRepository;
import demo.mable.character.repository.UserCharacterRepository;
import demo.mable.character.repository.UserSelecteCharacterRespository;
import demo.mable.skill.entity.CharacterSkill;
import demo.mable.skill.repository.CharacterSkillRepo;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCharacterService {

    private final UserCharacterRepository userCharacterRepository;
    private final CharacterRepository characterRepository;
    private final UserSelecteCharacterRespository userSelecteCharacterRespository;
    private final UserRepository userRepository;
    private final CharacterSkillRepo characterSkillRepo;

    public boolean findExistsByUserId(Long userId) {
        Optional<UserSelectedCharacter> byUserId = userSelecteCharacterRespository.findByUserId(userId);
        if(byUserId.isEmpty()) {
            return true;
        }
        return false;
    }
    public List<UserCharactersResponse> getCharactersByUserId(Long userId) {
        List<UserCharacter> UserCharacters = userCharacterRepository.findByUserIdOrderByCharacterRank(userId);
//        List<Character> characters = new ArrayList<>();
        for(UserCharacter userCharacter : UserCharacters) {
            if(!userCharacter.getUser().getId().equals(userId)){
                throw new UserIdMisMatchException();
            }
//            characters.add(userCharacter.getCharacter());
        }

        return UserCharacters.stream().map(UserCharactersResponse::from).toList();
    }

    public CharacterCardForLobbyResponse findSelectedCharacterCardByUserId(Long userId) {
        int charactersCount = userCharacterRepository.findByUser_Id(userId).size();
        UserSelectedCharacter userSelectedCharacter = userSelecteCharacterRespository.findByUserId(userId).orElseThrow(UserSelectedCharacterNotFoundException::new);
        return CharacterCardForLobbyResponse.from(userSelectedCharacter.getUserCharacter().getCharacter().getCharacterName(), userSelectedCharacter.getUserCharacter().getCharacter().getImagePath(), charactersCount);
    }
    @Transactional
    public UserSelectedCharacter selectCharacter(Long userId, Long characterId) {
        List<UserCharacter> byStatus = userCharacterRepository.findByStatus(UserCharacterStatus.SELECTED);
        for (UserCharacter status : byStatus) {
            status.statusChange(UserCharacterStatus.ACTIVE);
        }

        if(userId == null) {
            throw new IllegalArgumentException("userId는 null이여선 안됩니다.");
        }
        if(characterId == null) {
            throw new IllegalArgumentException("characterId는 null이여선 안됩니다.");
        }

        List<UserCharacter> userCharacters = userCharacterRepository.findByUserIdOrderByCharacterRank(userId);
        boolean isCharacter = userCharacters.stream().anyMatch(characters -> characters.getId().equals(characterId));

        if(!isCharacter) {
            throw new CharacterCardNotOwnedByUserException();
        }

        UserCharacter userCharacter = userCharacters.stream().filter(uc -> uc.getId().equals(characterId)).findFirst().orElseThrow(IllegalArgumentException::new);
        userCharacter.statusChange(UserCharacterStatus.SELECTED);

        UserSelectedCharacter usc = new UserSelectedCharacter();
        usc.assignUser(userCharacter.getUser());
        usc.assignCharacter(userCharacter);
        usc.updateTime();
        return userSelecteCharacterRespository.save(usc);
    }

    @Transactional
    public void selectCharacterChange(Long userId, Long userCharacterId) {
        System.out.println("characterId = " + userCharacterId);
        UserSelectedCharacter usc = userSelecteCharacterRespository.findByUserId(userId).orElseThrow(UserSelectedCharacterNotFoundException::new);
        usc.getUserCharacter().statusChange(UserCharacterStatus.ACTIVE);
        List<UserCharacter> ownedCharacters = userCharacterRepository.findByUser_Id(userId);
        boolean isCharacter = ownedCharacters.stream().anyMatch(card -> card.getId().equals(userCharacterId));


        if(!isCharacter) {
            throw new CharacterCardNotOwnedByUserException();
        }
//        if (!ownedCharacters.get(idx).getCharacter().getId().equals(characterId)) {
//            throw new IllegalArgumentException("캐릭터 리스트에서 선택한 캐릭터와 요청보낸 캐릭터 id가 일치하지 않습니다.");
//        }
        UserCharacter userCharacter = ownedCharacters.stream()
                .filter(usch -> usch.getId().equals(userCharacterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("selectCharacterChange: Exception"));

        usc.changeCharacter(userCharacter);
        usc.getUserCharacter().statusChange(UserCharacterStatus.SELECTED);
    }

    @Transactional
    public List<DrawnCardResponse> drawCards(Long userId, String cardPack) {
        int cardCountToDraw = 0;

        if(cardPack.equals("premium10")) {
            cardCountToDraw = 10;
        } else if(cardPack.equals("premium30")) {
            cardCountToDraw = 30;
        }
        List<UserCharacter> userCharacters = userCharacterRepository.findByUserIdOrderByCharacterRank(userId);
        List<DrawnCardResponse> drawnCards = new ArrayList<>(cardCountToDraw);
        List<Character> characters = characterRepository.findAll();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if((userCharacters.size() + cardCountToDraw) > 200) {
            throw new UserCharacterLimitExceededException();
        }

        for(int i = 0; i < cardCountToDraw; i++) {
            double rankRoll = (Math.random() * 100.0);

            if(rankRoll <= 0.1) {
                System.out.println(i +": S+  "+ rankRoll);
                drawnCards.add(findCharactersByRank(characters,"S+", user));
            } else if(rankRoll <= 1) {
                System.out.println(i +": s  "+ rankRoll);
                drawnCards.add(findCharactersByRank(characters,"S", user));
            } else if(rankRoll <= 20) {
                System.out.println(i +": A  "+ rankRoll);
                drawnCards.add(findCharactersByRank(characters,"A", user));
            } else {
                System.out.println(i +": B  "+ rankRoll);
                drawnCards.add(findCharactersByRank(characters,"B", user));
            }
        }
        System.out.println("drawnCards.size() = " + drawnCards.size());
        return drawnCards;
    }

    @Transactional
    public CharacterSynthesisResponse synthesizeCharacter(Long userId, CharacterSynthesisRequest request) {
        if(request == null) {
            throw new NullPointerException("합성에 필요한 데이터가 없습니다.");
        }
        if(request.getLeftCharacterId() == null || request.getRightCharacterId() == null) {
            throw new IllegalArgumentException("합성에 필요한 캐릭터는 2개입니다.");
        }
        if(request.getLeftCharacterId().equals(request.getRightCharacterId())) {
            throw new IllegalArgumentException("같은 캐릭터로 합성은 불가능 합니디.");
        }

        List<UserCharacter> findUserCharacters = userCharacterRepository.findUserCharactersByCharactersId(userId,request.getLeftCharacterId(), request.getRightCharacterId());

        if(!findUserCharacters.getFirst().getCharacter().getRank().equals(findUserCharacters.getLast().getCharacter().getRank())) {
            throw new CharacterRankMismatchException();
        }

        for (UserCharacter findUserCharacter : findUserCharacters) {
            findUserCharacter.statusChange(UserCharacterStatus.CONSUMED);
        }

        String characterRank = findUserCharacters.getFirst().getCharacter().getRank();
        List<Character> characterByRank = new ArrayList<>();

        switch (characterRank) {
            case "B":
                characterByRank = characterRepository.findCharacterByRank("A");
                break;
            case "A":
                characterByRank = characterRepository.findCharacterByRank("S");
                break;
            case "S","S+":
                characterByRank = characterRepository.findCharacterByRank("S+");
                break;
            default:
                throw new IllegalArgumentException("UserCharacterService: synthesizeCharacter : Exception ");
        }

        User user = findUserCharacters.getFirst().getUser();
        int randomValue = (int) (Math.random() * characterByRank.size());
        Character synthesisResult = characterByRank.get(randomValue);

        UserCharacter userCharacter = new UserCharacter();
        userCharacter.assignCharacter(synthesisResult);
        userCharacter.assignUser(user);

        userCharacterRepository.save(userCharacter);
        return new CharacterSynthesisResponse(synthesisResult.getImagePath());
    }

    private DrawnCardResponse findCharactersByRank(List<Character> characters, String rank, User user) {
        List<Character> list = characters.stream().filter(character -> character.getRank().equals(rank)).toList();
        UserCharacter userCharacter = new UserCharacter();
        int num = (int) (Math.random() * list.size());

        userCharacter.assignUser(user);
        userCharacter.assignCharacter(list.get(num));

        userCharacterRepository.save(userCharacter);

        return new DrawnCardResponse(list.get(num).getCharacterName(), list.get(num).getImagePath(), list.get(num).getRank());
    }

    public UserCharactersResponse findSelectedCharacterByUserId(Long userId) {
        UserSelectedCharacter userSelectedCharacter = userSelecteCharacterRespository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("선택한 캐릭터 카드가 없습니다."));
        List<CharacterSkill> characterSkills = characterSkillRepo.findByCharacter_Id(userSelectedCharacter.getUserCharacter().getCharacter().getId());
        List<SkillResponse> list = characterSkills.stream().map(SkillResponse::from).toList();
        return UserCharactersResponse.from(userSelectedCharacter, list);
    }
}
