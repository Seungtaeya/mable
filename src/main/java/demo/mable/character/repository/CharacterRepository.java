package demo.mable.character.repository;

import demo.mable.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    List<Character> findCharacterByRank(String rank);

    List<Character> findAllByRank(String rank);
}
