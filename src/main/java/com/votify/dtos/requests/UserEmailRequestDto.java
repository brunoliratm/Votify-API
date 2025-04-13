package com.votify.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailRequestDto(
        @NotBlank(message = "{users.email.NotBlank}")
        @Email(message = "{auth.email.Email}")
        String email
)
{}