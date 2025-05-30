package com.votify.controllers;

import com.votify.dtos.requests.UserPutRequestDto;
import com.votify.dtos.requests.UserRequestDto;
import com.votify.dtos.responses.UserResponseDTO;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.enums.UserRole;
import com.votify.facades.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/${api.version}/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Operation(summary = "Create a new user", description = "Create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "message": "Validation error",
                                                    "errors": [
                                                        "name: Name cannot be blank",
                                                        "name: Name must be between 1 and 100 characters",
                                                        "surname: Surname cannot be blank",
                                                        "surname: Surname must be between 1 and 100 characters",
                                                        "password: Password cannot be blank",
                                                        "password: Password must be between 6 and 50 characters",
                                                        "email: Email cannot be blank",
                                                        "email: Invalid email format",
                                                        "role: Role cannot be blank",
                                                        "role: Invalid role. Allowed values are ADMIN, ORGANIZER, ASSOCIATE"
                                                    ]
                                                }
                                            """))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                            )),
                    @ApiResponse(responseCode = "409", description = "Email conflict",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Email already exists\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred. Please contact support.\"}")))
            })
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDto userRequestDto,
                                           BindingResult bindingResult) {
        userFacade.create(userRequestDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update user", description = "Update user by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User updated"),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "message": "Validation error",
                                                    "errors": [
                                                        "name: Name cannot be blank",
                                                        "name: Name must be between 1 and 100 characters",
                                                        "surname: Surname cannot be blank",
                                                        "surname: Surname must be between 1 and 100 characters",
                                                        "password: Password cannot be blank",
                                                        "password: Password must be between 6 and 50 characters",
                                                        "email: Email cannot be blank",
                                                        "email: Invalid email format",
                                                        "role: Role cannot be blank",
                                                        "role: Invalid role. Allowed values are ADMIN, ORGANIZER, ASSOCIATE"
                                                    ]
                                                }
                                            """))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                            )),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"User not found\"}"))),
                    @ApiResponse(responseCode = "409", description = "Email conflict",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Email already exists\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred. Please contact support.\"}")))
            })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @RequestBody @Valid UserPutRequestDto userRequestDto, BindingResult bindingResult) {
        userFacade.update(id, userRequestDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete user", description = "Delete user by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}"))
                    ),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}"))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"User not found\"}"))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred. Please contact support.\"}"))
                    )
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userFacade.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get all users", description = "Get all users with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "info": {
                                                        "count": 100,
                                                        "pages": 10,
                                                        "next": "/api/v1/users?page=2",
                                                        "prev": null
                                                    },
                                                    "results": [
                                                        {
                                                            "name": "John",
                                                            "surname": "Doe",
                                                            "email": "john.doe@example.com",
                                                            "role": "ADMIN"
                                                        }
                                                    ]
                                                }
                                            """))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Invalid parameters\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred. Please contact support.\"}")))
            })
    @GetMapping
    public ResponseEntity<ApiResponseDto<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserRole role) {
        ApiResponseDto<UserResponseDTO> response = userFacade.getAll(page, name, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get user by id", description = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "id": 1,
                                                    "name": "John",
                                                    "surname": "Doe",
                                                    "email": "john.doe@example.com",
                                                    "role": "ADMIN"
                                                }
                                            """))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                            )),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"User not found\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred. Please contact support.\"}")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userFacade.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
