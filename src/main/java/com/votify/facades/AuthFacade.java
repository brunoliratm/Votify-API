package com.votify.facades;

import com.votify.dtos.requests.AuthenticationRequestDto;
import com.votify.dtos.requests.ResetPasswordRequestDto;
import com.votify.services.AuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {
    private final AuthService authService;

    public AuthFacade(AuthService authService) {
        this.authService = authService;
    }

    public String login(AuthenticationRequestDto authenticationRequestDTO) {
        return this.authService.login(authenticationRequestDTO);
    }

    public void forgotPassword(String email) {
        this.authService.forgotPassword(email);
    }

    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        this.authService.resetPassword(
                resetPasswordRequestDto.email(),
                resetPasswordRequestDto.code(),
                resetPasswordRequestDto.password(),
                resetPasswordRequestDto.confirmPassword()
        );
    }
}
