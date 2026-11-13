package demo.mable.character.repository;

import demo.mable.character.entity.UserCharacter;
import demo.mable.character.enums.UserCharacterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {

    @Query( """
        select uc from UserCharacter uc  join fetch uc.character ch where uc.user.id = :userId and uc.status Not in ("CONSUMED","REMOVED")
        order by case 
        when ch.rank = 'S+' then 0 
        when ch.rank = 'S' then 1
        when ch.rank = 'A' then 2
        when ch.rank = 'B' then 3
        end
""")
    List<UserCharacter> findByUserIdOrderByCharacterRank(@Param("userId") Long userId);

    List<UserCharacter> findByUser_Id(Long userId);

    @Query("""
        select uc from UserCharacter uc join fetch Character ch on uc.character.id = ch.id 
        where uc.user.id = :userId and uc.id in (:leftId, :rightId)
""")
    List<UserCharacter> findUserCharactersByCharactersId(@Param("userId") Long userId, @Param("leftId") Long leftCharacterId, @Param("rightId") Long rightCharacterId);

    @Modifying
    @Query(value = """
          UPDATE user_characters set character_status = :status 
""", nativeQuery = true)
    void updateStatus(@Param("status") String status);

    List<UserCharacter> findByStatus(UserCharacterStatus status);
}
