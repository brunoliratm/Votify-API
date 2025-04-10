package com.votify.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.votify.dtos.responses.UserResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.votify.exceptions.ConflictException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.requests.UserRequestDTO;
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

    public void createUser(UserRequestDTO userRequestDto, BindingResult bindingResult) {
        validateFieldsWithCheckEmail(userRequestDto, bindingResult);

        UserModel userModel = new UserModel();
        userModel.setName(userRequestDto.name());
        userModel.setSurname(userRequestDto.surname());
        userModel.setEmail(userRequestDto.email());
        userModel.setPassword(new BCryptPasswordEncoder().encode(userRequestDto.password()));

        if (userRequestDto.role() != null && !userRequestDto.role().isEmpty()) {
            try {
                userModel.setRole(UserRole.valueOf(userRequestDto.role().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException(List.of("Invalid role"));
            }
        } else {
            userModel.setRole(UserRole.ASSOCIATE);
        }
        userRepository.save(userModel);
    }

    public ApiResponseDto<UserResponseDTO> getAllUsers(int page, String name, UserRole role) {
        String roleValue;
        if (role != null) {
            roleValue = role.getRole();
        } else {
            roleValue = null;
        }

        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10);
        Page<UserModel> userPage;

        if (name != null && !name.isEmpty() && roleValue != null && !roleValue.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(roleValue.toUpperCase());
                userPage = userRepository.findByDeletedAtIsNullAndNameContainingIgnoreCaseAndRole(name, userRole, pageable);
            } catch (IllegalArgumentException e) {
                userPage = userRepository.findByDeletedAtIsNullAndNameContainingIgnoreCase(name, pageable);
            }
        } else if (name != null && !name.isEmpty()) {
            userPage = userRepository.findByDeletedAtIsNullAndNameContainingIgnoreCase(name, pageable);
        } else if (role != null && !roleValue.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(roleValue.toUpperCase());
                userPage = userRepository.findByDeletedAtIsNullAndRole(userRole, pageable);
            } catch (IllegalArgumentException e) {
                userPage = userRepository.findByDeletedAtIsNull(pageable);
            }
        } else {
            userPage = userRepository.findByDeletedAtIsNull(pageable);
        }

        List<UserResponseDTO> userRequestDtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        InfoDto info = utilHelper.buildPageableInfoDto(userPage, "/users");

        return new ApiResponseDto<>(info, userRequestDtos);
    }

    public UserResponseDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        return convertToDTO(user);
    }

    private UserResponseDTO convertToDTO(UserModel userModel) {
        return new UserResponseDTO(userModel.getName(), userModel.getSurname(), userModel.getEmail(),
                userModel.getRole() != null ? userModel.getRole().toString() : null);
    }

    private void validateFieldsWithCheckEmail(UserRequestDTO userRequestDto, BindingResult bindingResult) {
        Optional<UserModel> checkEmailExists = userRepository.findByEmail(userRequestDto.email());

        if (checkEmailExists.isPresent())
            throw new ConflictException("Email already exists");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    private void validateUpdateUser(Long id, UserRequestDTO userRequestDto, BindingResult bindingResult) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }

        if (userRequestDto.email() != null && !userRequestDto.email().isEmpty()
                && !userRequestDto.email().equals(user.getEmail())) {
            Optional<UserModel> existingUserWithEmail = userRepository.findByEmail(userRequestDto.email());
            if (existingUserWithEmail.isPresent()) {
                throw new ConflictException("Email already exists");
            }
        }

        if (userRequestDto.role() != null && !userRequestDto.role().isEmpty()) {
            try {
                UserRole.valueOf(userRequestDto.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException(List.of("Invalid role"));
            }
        }
    }

    private boolean isValidField(String field) {
        return field != null && !field.isEmpty();
    }

    public void updateUser(Long id, UserRequestDTO userRequestDto, BindingResult bindingResult) {
        validateUpdateUser(id, userRequestDto, bindingResult);

        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (isValidField(userRequestDto.email()) && !userRequestDto.email().equals(user.getEmail())) {
            user.setEmail(userRequestDto.email());
        }

        if (isValidField(userRequestDto.name())) {
            user.setName(userRequestDto.name());
        }

        if (isValidField(userRequestDto.surname())) {
            user.setSurname(userRequestDto.surname());
        }

        if (isValidField(userRequestDto.password())) {
            user.setPassword(userRequestDto.password());
        }

        if (isValidField(userRequestDto.role())) {
            user.setRole(UserRole.valueOf(userRequestDto.role().toUpperCase()));
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

    @Transactional
    public void updateUser(UserModel user) {
        userRepository.save(user);
    }
}
