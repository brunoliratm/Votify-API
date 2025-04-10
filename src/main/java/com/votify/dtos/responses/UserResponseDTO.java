package com.votify.dtos.responses;

public record UserResponseDTO(
        String name,
        String surname,
        String email,
        String role
) {}