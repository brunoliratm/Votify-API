package com.votify.dtos.responses;

public record UserResponseDTO(
        Long id,
        String name,
        String surname,
        String email,
        String role
) {}