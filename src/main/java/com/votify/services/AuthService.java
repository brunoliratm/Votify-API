package com.votify.services;

import com.votify.dtos.requests.AuthenticationRequestDTO;
import com.votify.exceptions.InvalidCredentialsException;
import com.votify.exceptions.InvalidResetCodeException;
import com.votify.exceptions.UserDeletedException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        return userService.loadUserByLogin(username);
    }
    public String login(AuthenticationRequestDTO loginDTO) {
        validateLogin(loginDTO);
        return tokenService.createToken(loginDTO);
    }

    private void validateLogin(AuthenticationRequestDTO loginDTO) {
        if (loginDTO.email() == null || loginDTO.password() == null) {
            throw new InvalidCredentialsException("Email and password are required");
        } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Email format is incorrect");
        }

        UserModel user;

        try {
            user = userService.loadUserByLogin(loginDTO.email());
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException("Email or password does not match");
        }

        if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.getPassword()))
            throw new InvalidCredentialsException("Email or password does not match");

        if (user.isDeleted()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            throw new UserDeletedException("this user has been deleted " + user.getDeletedAt().format(formatter));
        }
    }

    public void forgotPassword(String email) {
        if (email == null || email.isEmpty())
            throw new InvalidCredentialsException("Email is mandatory");
        if (!email.matches("^[^@]+@[^@]+$"))
            throw new InvalidCredentialsException("Invalid email format");

        UserModel user = userService.loadUserByLogin(email);

        String resetCode = RandomCodeGenService.generateRandomCode(6);
        user.setResetPasswordCode(resetCode);
        user.setResetPasswordExpirationCode(LocalDateTime.now().plusMinutes(5));
        userService.updateUser(user);
        emailService.sendResetPasswordEmail(email, resetCode);


    }

    public void resetPassword(String email, String code, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new InvalidCredentialsException("Passwords do not match");
        }
        if (email == null || !email.matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Invalid email format");
        }
        UserModel user = userService.loadUserByLogin(email);

        if (user.isDeleted())
            throw new UserNotFoundException();

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
