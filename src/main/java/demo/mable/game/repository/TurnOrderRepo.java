package demo.mable.game.repository;

import demo.mable.game.entity.Game;
import demo.mable.game.entity.TurnOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnOrderRepo extends JpaRepository<TurnOrder, Long> {

    List<TurnOrder> findByGame_Id(Long gameId);

    boolean existsByGame_IdAndUser_Username(Long gameId, String userUsername);
}
