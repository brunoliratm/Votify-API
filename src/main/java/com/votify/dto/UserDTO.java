package com.votify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
    @NotBlank(message = "{name.NotBlank}")
    @Size(min = 1, max = 100, message = "{name.Size}")
    String name, 
    @NotBlank(message = "{surname.NotBlank}")
    @Size(min = 1, max = 100, message = "{surname.Size}")
    String surname,
    @NotBlank(message = "{password.NotBlank}")
    @Size(min = 6, max = 50, message = "{password.Size}")
    String password,
    @Email(message = "{email.Email}") 
    String email, 
    String role) {}
