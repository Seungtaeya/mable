package demo.mable.game.repository;

import demo.mable.game.entity.GameProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GamePropertiesRepository extends JpaRepository<GameProperties, Long> {

    @Query("""
        select gp from GameProperties gp where gp.game.id = :gameId and gp.tileId = :tileId
""")
    GameProperties findByGameIdAndTileId(Long gameId, int tileId);

    boolean existsByGame_IdAndTileId(Long gameId, int tileId);

    void deleteAllByPlayer_Id(Long playerId);

    List<GameProperties> findByGame_IdAndPlayer_Id(Long gameId, Long playerId);

    @Query("""
        select p.tileId from GameProperties p where p.game.id = :gameId and p.player.id = :playerId 
        And (p.landmark = false) 
""")
    List<Integer>findPropertiesMissingAnyBuilding(Long gameId, Long playerId);
}
