package com.votify.facades;

import com.votify.dtos.requests.AuthenticationRequestDto;
import com.votify.dtos.requests.ResetPasswordRequestDto;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.interfaces.IAuthService;
import com.votify.interfaces.IUserService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {
    private final IAuthService authService;
    private final IUserService userService;

    public AuthFacade(IAuthService authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public String login(AuthenticationRequestDto authenticationRequestDTO) {
        return this.authService.login(authenticationRequestDTO);
    }

    public UserResponseDTO getCurrentUserInfo(Long userId) {
        return this.userService.getUserById(userId);
    }

    public void forgotPassword(String email) {
        this.authService.forgotPassword(email);
    }

    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        this.authService.resetPassword(resetPasswordRequestDto.email(),
                resetPasswordRequestDto.code(), resetPasswordRequestDto.password(),
                resetPasswordRequestDto.confirmPassword());
    }
}
