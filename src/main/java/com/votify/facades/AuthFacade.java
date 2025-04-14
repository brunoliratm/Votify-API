package com.votify.facades;

import com.votify.dtos.requests.AuthenticationRequestDTO;
import com.votify.dtos.requests.ResetPasswordRequestDTO;
import com.votify.services.AuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {
    private final AuthService authService;

    public AuthFacade(AuthService authService) {
        this.authService = authService;
    }

    public String login(AuthenticationRequestDTO authenticationRequestDTO) {
        return this.authService.login(authenticationRequestDTO);
    }

    public void forgotPassword(String email) {
        this.authService.forgotPassword(email);
    }

    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDto) {
        this.authService.resetPassword(
                resetPasswordRequestDto.email(),
                resetPasswordRequestDto.code(),
                resetPasswordRequestDto.password(),
                resetPasswordRequestDto.confirmPassword()
        );
    }
}
