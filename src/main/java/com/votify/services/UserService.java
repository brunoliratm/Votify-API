package com.votify.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.votify.dto.UserDTO;
import com.votify.exceptions.ConflictException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import com.votify.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        }

        userRepository.save(userModel);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().filter(UserModel::isActive).map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserNotFoundException();
        }

        return convertToDTO(user);
    }

    private UserDTO convertToDTO(UserModel userModel) {
        return new UserDTO(userModel.getName(), userModel.getSurname(), null, userModel.getEmail(),
                userModel.getRole() != null ? userModel.getRole().toString() : null);
    }

    private void validateFieldsWithCheckEmail(UserDTO userDto, BindingResult bindingResult) {
        UserDetails checkEmailExists = userRepository.findByEmail(userDto.email());

        if (checkEmailExists != null )
            throw new ConflictException("Email already exists");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    private void validateUpdateUser(Long id, UserDTO userDto, BindingResult bindingResult) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserNotFoundException();
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }

        if (userDto.email() != null && !userDto.email().isEmpty()
                && !userDto.email().equals(user.getEmail())) {
            UserDetails existingUserWithEmail = userRepository.findByEmail(userDto.email());
            if (existingUserWithEmail.getUsername().equals(user.getEmail())) {
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
        user.setActive(false);
        userRepository.save(user);
    }

    public UserDetails loadUserByLogin(String login) throws UserNotFoundException {
        return userRepository.findByEmail(login);
    }

}
