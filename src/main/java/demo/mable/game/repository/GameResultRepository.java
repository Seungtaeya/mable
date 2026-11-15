package demo.mable.game.repository;

import demo.mable.game.entity.GameResult;
import demo.mable.user.dto.UserRankingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    @Query(value = """
        select new demo.mable.user.dto.UserRankingResponse(
            user.username,
            count(gr.winner)
        ) from User user
        left join GameResult gr on gr.user = user and gr.winner = true
        group by user.id, user.username
        order by count(gr.winner) desc , user.id asc
        limit 10 
        offset :offset 
""")
    List<UserRankingResponse> findRankings(Integer offset);
}
