package com.votify.dtos;

import jakarta.validation.constraints.Email;

public record UserEmailDto(

        @Email(message = "{auth.email.Email}")
        String email
)
{}