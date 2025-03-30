package com.votify.services;

import com.votify.dto.AuthenticationDto;
import com.votify.exceptions.InvalidCredentialsException;
import com.votify.exceptions.UserInactiveException;
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
        return userService.loadUserByLogin(username).get();
    }
    public String login(AuthenticationDto loginDTO) {
        validateLogin(loginDTO);
        return tokenService.createToken(loginDTO);
    }

    private void validateLogin(AuthenticationDto loginDTO) {
        try {
            if (loginDTO.email() == null || loginDTO.password() == null) {
                throw new InvalidCredentialsException("Email and password are required");
            } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
                throw new InvalidCredentialsException("Email format is incorrect");
            }

            Optional<UserModel> user = userService.loadUserByLogin(loginDTO.email());

            if (user.isEmpty())
                throw new UserNotFoundException();

            if (!user.get().isEnabled())
                throw new UserInactiveException("User is inactive");

            if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
                throw new InvalidCredentialsException("Email or password does not match");
            }

        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (UserInactiveException e) {
            throw new UserInactiveException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during login");
        }
    }
}
