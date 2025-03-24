package com.votify.controllers;

import com.votify.dtos.SessionDto;
import com.votify.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/sessions")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @Operation(summary = "Create a new session", description = "Create a new session", responses = {
            @ApiResponse(responseCode = "201", description = "Session created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid SessionDto sessionDto,
            BindingResult bindingResult
    ) {
        this.sessionService.save(sessionDto, bindingResult);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Get all users", description = "Get all users", responses = {
            @ApiResponse(responseCode = "200", description = "List of users")
    })
    @GetMapping
    public ResponseEntity<List<SessionDto>> getAll() {
        List<SessionDto> sessions = sessionService.findAll();
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
}
