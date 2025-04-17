package com.votify.facades;

import com.votify.dtos.requests.UserPutRequestDto;
import com.votify.dtos.requests.UserRequestDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.enums.UserRole;
import com.votify.interfaces.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class UserFacade {
    private final IUserService userService;

    public UserFacade(IUserService userService) {
        this.userService = userService;
    }

    public void create(UserRequestDto userRequest, BindingResult bindingResult) {
        this.userService.createUser(userRequest, bindingResult);
    }

    public ApiResponseDto<UserResponseDTO> getAll(int page, String name, UserRole role) {
        return this.userService.getAllUsers(page, name, role);
    }

    public UserResponseDTO getById(Long id) {
        return this.userService.getUserById(id);
    }

    public void update(Long id, UserPutRequestDto userRequest, BindingResult bindingResult) {
        this.userService.updateUser(id, userRequest, bindingResult);
    }

    public void delete(Long id) {
        this.userService.deleteUser(id);
    }
    
}
