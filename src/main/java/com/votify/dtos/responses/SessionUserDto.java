package com.votify.dtos.responses;

import com.votify.enums.UserRole;

public record SessionUserDto(
    Long id,
    String name,
    String email,
    UserRole role
) {
}
