package com.votify.controllers;

import com.votify.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/sessions")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @Operation(summary = "Create a new user", description = "Create a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping
    public ResponseEntity<Void> create() {
        return ResponseEntity.ok().build();
    }
}
