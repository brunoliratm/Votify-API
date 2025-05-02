package com.votify.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.exceptions.InvalidCredentialsException;
import com.votify.interfaces.IUserService;
import com.votify.interfaces.UserRoleInterface;
import jakarta.transaction.Transactional;
import com.votify.exceptions.ConflictException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.requests.UserPutRequestDto;
import com.votify.dtos.requests.UserRequestDto;
import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import com.votify.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UtilHelper utilHelper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UtilHelper utilHelper) {
        this.userRepository = userRepository;
        this.utilHelper = utilHelper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    @Override
    public void createUser(UserRequestDto userRequestDto, BindingResult bindingResult) {
        validateFieldsWithCheckEmail(userRequestDto, bindingResult);

        validateUserRole(userRequestDto);

        UserModel userModel = new UserModel();
        userModel.setName(userRequestDto.name());
        userModel.setSurname(userRequestDto.surname());
        userModel.setEmail(userRequestDto.email());

        String encryptedPassword = passwordEncoder.encode(userRequestDto.password());
        userModel.setPassword(encryptedPassword);

        if (!userRequestDto.role().isEmpty()) {
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

    @Override
    public ApiResponseDto<UserResponseDTO> getAllUsers(int page, String name, UserRole role) {
        int pageIndex = (page > 0) ? (page - 1) : 0;
        Pageable pageable = PageRequest.of(pageIndex, 10);

        Page<UserModel> userPage = userRepository
            .findByFilters((name != null && !name.isEmpty()) ? name : null, role, pageable);

        List<UserResponseDTO> userResponseDtos =
            userPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());

        InfoDto info = utilHelper.buildPageableInfoDto(userPage, "users");

        return new ApiResponseDto<>(info, userResponseDtos);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        return convertToDTO(user);
    }

    private UserResponseDTO convertToDTO(UserModel userModel) {
        return new UserResponseDTO(userModel.getId(), userModel.getName(), userModel.getSurname(),
            userModel.getEmail(),
            userModel.getRole() != null ? userModel.getRole().toString() : null);
    }

    private void validateFieldsWithCheckEmail(UserRequestDto userRequestDto,
                                              BindingResult bindingResult) {
        Optional<UserModel> checkEmailExists = userRepository.findByEmail(userRequestDto.email());

        if (checkEmailExists.isPresent())
            throw new ConflictException("Email already exists");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    private void validateUpdateUser(Long id, UserPutRequestDto userRequestDto,
                                    BindingResult bindingResult) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }

        validateUserRole(userRequestDto);

        if (userRequestDto.email() != null && !userRequestDto.email().isEmpty()
            && !userRequestDto.email().equals(user.getEmail())) {
            Optional<UserModel> existingUserWithEmail =
                userRepository.findByEmail(userRequestDto.email());
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

    @Transactional
    @Override
    public void updateUser(Long id, UserPutRequestDto userPutRequestDto, BindingResult bindingResult) {
        validateUpdateUser(id, userPutRequestDto, bindingResult);

        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (userPutRequestDto.email() != null
            && !userPutRequestDto.email().isEmpty()
            && !userPutRequestDto.email().equals(user.getEmail())) {
            user.setEmail(userPutRequestDto.email());
        }

        if (userPutRequestDto.name() != null && !userPutRequestDto.name().isEmpty()) {
            user.setName(userPutRequestDto.name());
        }

        if (userPutRequestDto.surname() != null && !userPutRequestDto.surname().isEmpty()) {
            user.setSurname(userPutRequestDto.surname());
        }

        if (userPutRequestDto.password() != null && !userPutRequestDto.password().isEmpty()) {
            user.setPassword(new BCryptPasswordEncoder().encode(userPutRequestDto.password()));
        }

        if (userPutRequestDto.role() != null && !userPutRequestDto.role().isEmpty()) {
            user.setRole(UserRole.valueOf(userPutRequestDto.role().toUpperCase()));
        }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = (UserModel) authentication.getPrincipal();

        if (currentUser.getRole() == UserRole.ORGANIZER) {
            if (!user.getRole().name().equalsIgnoreCase("ASSOCIATE")) {
                throw new InvalidCredentialsException("Organizers can only manipulate associates");
            }
        }

        resolveUserManipulate(user.getRole().name());

        user.delete();
        userRepository.save(user);
    }

    @Override
    public UserModel loadUserByLogin(String login) throws UserNotFoundException {
        return userRepository.findByEmail(login).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserModel findOrganizer(Long id) {
        Optional<UserModel> user = userRepository.findById(id);
        if (user.isEmpty() ||
            !user.get().getAuthorities().contains(new SimpleGrantedAuthority("ORGANIZER"))
            || user.get().getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
            || user.get().isDeleted()) {
            throw new UserNotFoundException("Organizer not found");
        }
        return user.get();
    }

    @Override
    @Transactional
    public void updateUser(UserModel user) {
        userRepository.save(user);
    }

    @Override
    public UserModel getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    private void validateUserRole(UserRoleInterface userDto) {
        if (userDto.role().equalsIgnoreCase(UserRole.ADMIN.name())) {
            throw new InvalidCredentialsException("It's not allowed to create an ADMIN user.");
        }

        resolveUserManipulate(userDto.role());
    }

    private void resolveUserManipulate(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = (UserModel) authentication.getPrincipal();

        if (currentUser.getRole() == UserRole.ORGANIZER) {
            if (!"ASSOCIATE".equalsIgnoreCase(role)) {
                throw new InvalidCredentialsException("Organizers can only manipulate associates.");
            }
        }
    }
}
