package com.votify.facades;

import com.votify.dtos.requests.AuthenticationRequestDto;
import com.votify.dtos.requests.ResetPasswordRequestDto;
import com.votify.interfaces.IAuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {
    private final IAuthService authService;

    public AuthFacade(IAuthService authService) {
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
