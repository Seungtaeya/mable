package demo.mable.room.dto;

import jakarta.validation.constraints.NotBlank;

public record GameRoomRequest(
        @NotBlank String title,  int maxPlayers) {
}
