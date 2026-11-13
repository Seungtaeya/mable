package demo.mable.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomPlayerJoinRequest(
        @NotNull
        @Positive
        Long roomId

        ) {
}
