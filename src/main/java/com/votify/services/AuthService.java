package com.votify.services;

import com.votify.dto.AuthenticationDto;
import com.votify.exceptions.*;
import com.votify.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final TokenService tokenService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public AuthService(UserService userService, TokenService tokenService, EmailService emailService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
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

    public void forgotPassword(String email) {
        if (email == null || email.isEmpty())
            throw new InvalidCredentialsException("Email is mandatory");
        if (!email.matches("^[^@]+@[^@]+$"))
            throw new InvalidCredentialsException("Invalid email format");

        Optional<UserModel> userOpt = userService.loadUserByLogin(email);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            String resetCode = RandomCodeGenService.generateRandomCode(6);
            user.setResetPasswordCode(resetCode);
            user.setResetPasswordExpirationCode(LocalDateTime.now().plusMinutes(5));
            userService.updateUser(user);
            emailService.sendResetPasswordEmail(email, resetCode);
        }

    }

    public void resetPassword(String email, String code, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new InvalidCredentialsException("Passwords do not match");
        }
        if (email == null || !email.matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Invalid email format");
        }
        Optional<UserModel> userOpt = userService.loadUserByLogin(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        if (!userOpt.get().isEnabled())
            throw new UserInactiveException("Inactive user");


        UserModel user = userOpt.get();

        if (user.getResetPasswordCode() == null || !user.getResetPasswordCode().equals(code)) {
            throw new InvalidResetCodeException("Invalid reset code");
        }
        if (user.getResetPasswordExpirationCode() == null ||
                user.getResetPasswordExpirationCode().isBefore(LocalDateTime.now())) {
            throw new InvalidResetCodeException("expired code");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setResetPasswordCode(null);
        user.setResetPasswordExpirationCode(null);
        userService.updateUser(user);
    }
}
