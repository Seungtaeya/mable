package demo.mable.room.repository;

import demo.mable.room.entity.GameRoom;
import demo.mable.room.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    List<RoomPlayer> findAllByRoomIdOrderByGameOrderAsc(Long roomId);

    RoomPlayer room(GameRoom room);

    List<RoomPlayer> findByRoomId(Long roomId);

    Optional<RoomPlayer> findByRoom_IdAndUser_Id(Long roomId, Long userId);
}
