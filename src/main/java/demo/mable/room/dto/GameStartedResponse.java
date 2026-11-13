package demo.mable.room.dto;

import demo.mable.room.enums.RoomActionType;

public record GameStartedResponse(Long gameId, Long roomId, Long userId, RoomActionType actionType, Boolean ready) {
}
