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
import com.votify.dtos.ApiResponseDto;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user", description = "Create a new user",
            responses = {
                @ApiResponse(responseCode = "201", description = "User created"),
                @ApiResponse(responseCode = "400", description = "Invalid input", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"Validation error\", \"errors\": [\"name: Name cannot be blank\", \"email: Invalid email format\"]}"))),
                @ApiResponse(responseCode = "409", description = "User already exists", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"Email already exists\"}")))
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
                @ApiResponse(responseCode = "400", description = "Invalid input", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"Validation error\", \"errors\": [\"name: Name cannot be blank\", \"email: Invalid email format\"]}"))),
                @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"User not found\"}"))),
                @ApiResponse(responseCode = "409", description = "Email conflict", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"Email already exists\"}")))
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
                @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"User not found\"}")))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get all users", description = "Get all users with pagination",
            responses = {
                @ApiResponse(responseCode = "200", description = "List of users"),
                @ApiResponse(responseCode = "400", description = "Bad request", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"Invalid parameters\"}")))
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
                @ApiResponse(responseCode = "200", description = "User found"),
                @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(mediaType = "application/json", 
                        examples = @ExampleObject(value = "{\"message\": \"User not found\"}")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
