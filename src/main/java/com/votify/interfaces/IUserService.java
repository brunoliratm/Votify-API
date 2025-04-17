package com.votify.interfaces;

import org.springframework.validation.BindingResult;
import com.votify.dtos.requests.UserPutRequestDto;
import com.votify.dtos.requests.UserRequestDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.enums.UserRole;
import com.votify.exceptions.UserNotFoundException;
import com.votify.models.UserModel;

public interface IUserService {
    void createUser(UserRequestDto userRequestDto, BindingResult bindingResult);

    ApiResponseDto<UserResponseDTO> getAllUsers(int page, String name, UserRole role);

    UserResponseDTO getUserById(Long id);

    void updateUser(Long id, UserPutRequestDto userPutRequestDto, BindingResult bindingResult);

    void deleteUser(Long id);

    UserModel loadUserByLogin(String login) throws UserNotFoundException;

    UserModel findOrganizer(Long id);

    void updateUser(UserModel user);

    UserModel getUserByEmail(String email);

}
