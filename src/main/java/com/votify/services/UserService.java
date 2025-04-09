package com.votify.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.votify.exceptions.ConflictException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.UserDTO;
import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import com.votify.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.votify.dtos.InfoDto;
import com.votify.helpers.UtilHelper;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UtilHelper utilHelper;

    public void createUser(UserDTO userDto, BindingResult bindingResult) {
        validateFieldsWithCheckEmail(userDto, bindingResult);

        UserModel userModel = new UserModel();
        userModel.setName(userDto.name());
        userModel.setSurname(userDto.surname());
        userModel.setEmail(userDto.email());
        userModel.setPassword(new BCryptPasswordEncoder().encode(userDto.password()));

        if (userDto.role() != null && !userDto.role().isEmpty()) {
            try {
                userModel.setRole(UserRole.valueOf(userDto.role().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException(List.of("Invalid role"));
            }
        } else {
            userModel.setRole(UserRole.ASSOCIATE);
        }
        userRepository.save(userModel);
    }

    public ApiResponseDto<UserDTO> getAllUsers(int page, String name, String role) {
        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10);
        Page<UserModel> userPage;

        if (name != null && !name.isEmpty() && role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                userPage = userRepository.findByActiveTrueAndNameContainingIgnoreCaseAndRole(name, userRole, pageable);
            } catch (IllegalArgumentException e) {
                userPage = userRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);
            }
        } else if (name != null && !name.isEmpty()) {
            userPage = userRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);
        } else if (role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                userPage = userRepository.findByActiveTrueAndRole(userRole, pageable);
            } catch (IllegalArgumentException e) {
                userPage = userRepository.findByActiveTrue(pageable);
            }
        } else {
            userPage = userRepository.findByActiveTrue(pageable);
        }

        List<UserDTO> userDtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        InfoDto info = utilHelper.buildPageableInfoDto(userPage, "/users");

        return new ApiResponseDto<>(info, userDtos);
    }

    public UserDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        return convertToDTO(user);
    }

    private UserDTO convertToDTO(UserModel userModel) {
        return new UserDTO(userModel.getName(), userModel.getSurname(), null, userModel.getEmail(),
                userModel.getRole() != null ? userModel.getRole().toString() : null);
    }

    private void validateFieldsWithCheckEmail(UserDTO userDto, BindingResult bindingResult) {
        Optional<UserModel> checkEmailExists = userRepository.findByEmail(userDto.email());

        if (checkEmailExists.isPresent())
            throw new ConflictException("Email already exists");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    private void validateUpdateUser(Long id, UserDTO userDto, BindingResult bindingResult) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }

        if (userDto.email() != null && !userDto.email().isEmpty()
                && !userDto.email().equals(user.getEmail())) {
            Optional<UserModel> existingUserWithEmail = userRepository.findByEmail(userDto.email());
            if (existingUserWithEmail.isPresent()) {
                throw new ConflictException("Email already exists");
            }
        }

        if (userDto.role() != null && !userDto.role().isEmpty()) {
            try {
                UserRole.valueOf(userDto.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException(List.of("Invalid role"));
            }
        }
    }

    private boolean isValidField(String field) {
        return field != null && !field.isEmpty();
    }

    public void updateUser(Long id, UserDTO userDto, BindingResult bindingResult) {
        validateUpdateUser(id, userDto, bindingResult);

        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (isValidField(userDto.email()) && !userDto.email().equals(user.getEmail())) {
            user.setEmail(userDto.email());
        }

        if (isValidField(userDto.name())) {
            user.setName(userDto.name());
        }

        if (isValidField(userDto.surname())) {
            user.setSurname(userDto.surname());
        }

        if (isValidField(userDto.password())) {
            user.setPassword(userDto.password());
        }

        if (isValidField(userDto.role())) {
            user.setRole(UserRole.valueOf(userDto.role().toUpperCase()));
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.delete();
        userRepository.save(user);
    }

    public UserModel loadUserByLogin(String login) throws UserNotFoundException {
        return userRepository.findByEmail(login).orElseThrow(UserNotFoundException::new);
    }

    public UserModel findOrganizer(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ORGANIZER"))) throw new UserNotFoundException();
        return user;
    }
}