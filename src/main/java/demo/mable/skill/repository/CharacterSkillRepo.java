package demo.mable.skill.repository;

import demo.mable.skill.entity.CharacterSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterSkillRepo extends JpaRepository<CharacterSkill, Long> {
    List<CharacterSkill> findByCharacter_Id(Long characterId);
}
