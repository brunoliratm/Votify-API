package com.votify.controllers;

import com.votify.dtos.UserDTO;
import com.votify.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.votify.dtos.responses.ApiResponseDto;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    @Autowired
    private UserService userService;

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
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")))
            })
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDTO userDto,
                                           BindingResult bindingResult) {
        userService.createUser(userDto, bindingResult);
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
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")))
            })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @RequestBody @Valid UserDTO userDto, BindingResult bindingResult) {
        userService.updateUser(id, userDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete user", description = "Delete user by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
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
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
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
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")))
            })
    @GetMapping
    public ResponseEntity<ApiResponseDto<UserDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role) {
        ApiResponseDto<UserDTO> response = userService.getAllUsers(page, name, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get user by id", description = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
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
                                    examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
