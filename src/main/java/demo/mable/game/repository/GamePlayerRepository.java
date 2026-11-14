package demo.mable.game.repository;

import demo.mable.character.dto.SkillResponse;
import demo.mable.game.dto.GamePlayerDetailResponse;
import demo.mable.game.dto.GamePlayerResponse;
import demo.mable.game.entity.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

    @Query("""
        select new demo.mable.game.dto.GamePlayerResponse(
        gp.id,
        gp.user.username,
        gp.turnOrder,
        gp.cash, 
        usc.userCharacter.character.imagePath,
        usc.userCharacter.character.pieceImage) from GamePlayer gp left join fetch UserSelectedCharacter usc on gp.user.id = usc.user.id where gp.game.id = :gameId
""")
    List<GamePlayerResponse> findPlayerSummariesByGameId(Long gameId);

    @Query(value = """
        select gp from GamePlayer gp
            join fetch gp.game
            join fetch gp.user
            where gp.game.id = :gameId and gp.user.username = :userName
        
""")
    GamePlayer findByGameIdAndUsername(Long gameId, String userName);

    @Query(value = """
        select new demo.mable.game.dto.GamePlayerDetailResponse(
            user.id,
            char.id,
            char.characterName,
            char.imagePath
        ) from GamePlayer gp 
        join  gp.user user 
        join  UserSelectedCharacter usc on usc.user = user
        join  usc.userCharacter uc
        join  uc.character char 
        where gp.id = :playerId
""")
    GamePlayerDetailResponse findPlayerDetailByPlayerId(Long playerId);

    @Query(value = """
        select new demo.mable.character.dto.SkillResponse(
            sk.imagePath,
            sk.skillDescription
        ) from Character ch
        left join CharacterSkill cs on cs.character = ch
        left join cs.skill sk
        where ch.id = :characterId
""")
    List<SkillResponse> findCharacterSkillsByCharacterId(Long characterId);

    List<GamePlayer> findByGame_Id(Long gameId);

    List<GamePlayer> findByGame_IdOrderByTurnOrderAsc(Long gameId);

}
