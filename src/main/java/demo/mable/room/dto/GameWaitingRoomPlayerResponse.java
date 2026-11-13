package demo.mable.room.dto;

public record GameWaitingRoomPlayerResponse(Long userId, String userName, boolean isHost, boolean ready) {
}
