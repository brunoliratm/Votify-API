package com.votify.facades;

import com.votify.dtos.requests.UserRequestDTO;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.enums.UserRole;
import com.votify.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class UserFacade {
    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public void create(UserRequestDTO userRequest, BindingResult bindingResult) {
        this.userService.createUser(userRequest, bindingResult);
    }

    public ApiResponseDto<UserResponseDTO> getAll(int page, String name, UserRole role) {
        return this.userService.getAllUsers(page, name, role);
    }

    public UserResponseDTO getById(Long id) {
        return this.userService.getUserById(id);
    }

    public void update(Long id, UserRequestDTO userRequest, BindingResult bindingResult) {
        this.userService.updateUser(id, userRequest, bindingResult);
    }

    public void delete(Long id) {
        this.userService.deleteUser(id);
    }
    
}
