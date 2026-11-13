package demo.mable.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String checkPassword,
        @NotBlank@Email
        String email) {
}
