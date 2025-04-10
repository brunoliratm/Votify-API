package com.votify.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "{users.name.NotBlank}")
    @Size(min = 1, max = 100, message = "{users.name.Size}")
    String name,
    @NotBlank(message = "{users.surname.NotBlank}")
    @Size(min = 1, max = 100, message = "{users.surname.Size}")
    String surname,
    @NotBlank(message = "{users.password.NotBlank}")
    @Size(min = 6, max = 50, message = "{users.password.Size}")
    String password,
    @NotBlank(message = "{users.email.NotBlank}")
    @Email(message = "{users.email.Email}")
    String email,
    @NotBlank(message = "{users.role.NotBlank}")
    @Pattern(regexp = "ADMIN|ORGANIZER|ASSOCIATE", message = "{users.role.Invalid}")
    String role
) {}
