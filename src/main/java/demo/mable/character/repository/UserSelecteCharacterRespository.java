package demo.mable.character.repository;

import demo.mable.character.entity.UserSelectedCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserSelecteCharacterRespository extends JpaRepository<UserSelectedCharacter, Long> {

    @Query(value = """
        select usc.id, usc.user_id, usc.user_character_id, usc.updated_at 
        from user_selected_character usc 
        where usc.user_id = :userId
""" ,nativeQuery = true)
    Optional<UserSelectedCharacter> findByUserId(Long userId);

}
