package com.votify.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.votify.dto.UserDTO;
import com.votify.exceptions.ConflictException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.models.UserModel;
import com.votify.repositories.UserRepository;
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
        userModel.setPassword(userDto.password());
        userRepository.save(userModel);
    }
  
    private void validateFieldsWithCheckEmail(UserDTO userDto, BindingResult bindingResult) {
        Optional<UserModel> checkEmailExists = userRepository.findByEmail(userDto.email());

        if (checkEmailExists.isPresent()) throw new ConflictException("Email já cadastrado");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }
}