package demo.mable.skill.repository;

import demo.mable.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepo extends JpaRepository<Skill, Long> {
}
