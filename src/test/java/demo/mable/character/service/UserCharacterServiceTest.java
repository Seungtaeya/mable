package demo.mable.character.service;

import demo.mable.character.dto.UserCharactersResponse;
import demo.mable.character.entity.UserSelectedCharacter;
import demo.mable.character.repository.UserCharacterRepository;
import demo.mable.character.repository.UserSelecteCharacterRespository;
import demo.mable.user.entity.User;
import demo.mable.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserCharacterServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCharacterService userCharacterService;
    @Autowired
    UserCharacterRepository userCharacterRepository;
    @Autowired
    UserSelecteCharacterRespository userSelecteCharacterRespository;

    @Transactional
    @Test
    public void test() throws Exception {
        //given
        User user = userRepository.findByUsername("qwe").orElseThrow(()-> new IllegalArgumentException("TestCodeException"));
        //when
        List<UserCharactersResponse> characters = userCharacterService.getCharactersByUserId(user.getId());
        //then
        assertThat(characters.getFirst().getCharacterName()).isEqualTo("프시케");
    }

    @Transactional
    @Test
    public void SelectCharacter() throws Exception {
        //given
        User user = userRepository.findByUsername("qwe").orElseThrow(()-> new IllegalArgumentException("TestCodeException"));
        //when
        UserSelectedCharacter us = userCharacterService.selectCharacter(user.getId(), 1L);
        System.out.println(us.getId());
        //then
//        assertThat(us.getCharacter().getId()).isEqualTo(1L);
//        assertThat(us.getCharacter().getCharacterName()).isEqualTo("프시케");

    }
}