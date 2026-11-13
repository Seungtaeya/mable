package demo.mable.game.repository;

import demo.mable.game.dto.GamePlayerResponse;
import demo.mable.game.entity.Game;
import demo.mable.game.entity.GamePlayer;
import demo.mable.user.entity.User;
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
    List<GamePlayer> findByGame_Id(Long gameId);

    List<GamePlayer> findByGame_IdOrderByTurnOrderAsc(Long gameId);

}
