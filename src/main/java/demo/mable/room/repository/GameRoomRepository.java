package demo.mable.room.repository;

import demo.mable.room.entity.GameRoom;
import demo.mable.room.enums.GameRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    List<GameRoom> findByTitle(String title);

    List<GameRoom> findAllByStatus(GameRoomStatus status);

    @Query("""
        select gr from GameRoom gr order by gr.id desc
""")
    List<GameRoom> findAllOrderByDesc();
}
