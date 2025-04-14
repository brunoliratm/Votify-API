package com.votify.dtos.requests;

import com.votify.interfaces.UserRoleInterface;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPutRequestDto(
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters.")
    String name, 

    @Size(min = 1, max = 100, message = "Surname must be between 1 and 100 characters.")
    String surname,

    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters.")
    String password,

    @Email(message = "Invalid email format.") 
    String email, 

    @Pattern(regexp = "ADMIN|ORGANIZER|ASSOCIATE", message = "Role must be one of the following: ADMIN, ORGANIZER, ASSOCIATE.")
    String role
) implements UserRoleInterface {}
