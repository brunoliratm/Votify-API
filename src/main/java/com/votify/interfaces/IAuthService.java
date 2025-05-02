package com.votify.interfaces;

import com.votify.dtos.requests.AuthenticationRequestDto;

public interface IAuthService {
    String login(AuthenticationRequestDto authenticationRequestDTO);

    void forgotPassword(String email);

    void resetPassword(String email, String code, String password, String confirmPassword);

}
