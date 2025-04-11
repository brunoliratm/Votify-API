package com.votify.services;

import com.votify.dtos.AuthenticationDto;
import com.votify.exceptions.InvalidCredentialsException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final TokenService tokenService;
    private final UserService userService;

    @Autowired
    public AuthService(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        return userService.loadUserByLogin(username);
    }
    public String login(AuthenticationDto loginDTO) {
        validateLogin(loginDTO);
        return tokenService.createToken(loginDTO);
    }

    private void validateLogin(AuthenticationDto loginDTO) {
        if (loginDTO.email() == null || loginDTO.password() == null) {
            throw new InvalidCredentialsException("Email and password are required");
        } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Email format is incorrect");
        }
        UserModel user = userService.loadUserByLogin(loginDTO.email());

        if (user == null)
            throw new UserNotFoundException();

        if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Email or password does not match");
        }
    }
}
