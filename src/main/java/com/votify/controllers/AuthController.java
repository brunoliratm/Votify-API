package com.votify.controllers;

import com.votify.dtos.requests.AuthenticationRequestDto;
import com.votify.dtos.requests.ResetPasswordRequestDto;
import com.votify.dtos.requests.UserEmailRequestDto;
import com.votify.facades.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/${api.version}/auth")
public class AuthController {
    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @Operation(summary = "User login", description = "User can login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Missing required fields",
                                            value = "{\"message\": \"Email and password are required\"}"),
                                    @ExampleObject(name = "Invalid email format",
                                            value = "{\"message\": \"Email format is incorrect\"}"),
                                    @ExampleObject(name = "Incorrect email or password",
                                            value = "{\"message\": \"Email or password does not match\"}")
                            })
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UserNotFound",
                                    value = "{\"message\": \"User not found\"}")
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Inactive user",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UserInactive",
                                    value = "{\"message\": \"User is inactive\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UnexpectedError",
                                    value = "{\"message\": \"An error occurred during login\"}")
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDTO) {
        String token = this.authFacade.login(authenticationRequestDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    @Operation(summary = "Forgot password", description = "Send an email with a code to reset the password", responses = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"Email cannot be blank\", \"Invalid email format\"]}"),
            }))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid UserEmailRequestDto userEmailRequestDTO) {
        this.authFacade.forgotPassword(userEmailRequestDTO.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset password", description = "Reset the user's password", responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid code", value = "{\"errors\": [\"code: The code is mandatory\", \"code: The code must be 6 characters long\"]}"),
                    @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"Password is mandatory\", \"Password must be at least 6 characters long\"]}"),
                    @ExampleObject(name = "Passwords don't match", value = "{\"errors\": [\"Passwords do not match.\"]}"),
                    @ExampleObject(name = "Code expired", value = "{\"message\": \"Expired code\"}"),

            })),
            @ApiResponse(responseCode = "403", description = "User inactive", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Inactive user\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"User not found\"}"))),
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequestDto) {
        this.authFacade.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok().build();
    }
}
