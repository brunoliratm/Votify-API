package com.votify.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDto(
        @NotBlank(message = "{users.email.NotBlank}")
        @Email(message = "{users.email.Email}")
        String email,
        @NotBlank(message = "{users.password.NotBlank}")
        @Size(min = 6, max = 50, message = "{users.password.Size}")
        String password) {
}