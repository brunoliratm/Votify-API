package com.votify.controllers;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.services.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Operation(summary = "Register a vote", description = "Allows the currently logged-in associate to vote on an agenda.", responses = {
            @ApiResponse(responseCode = "201", description = "Vote registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "403", description = "Access denied."),
            @ApiResponse(responseCode = "404", description = "Agenda not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @ApiResponse(responseCode = "201", description = "Vote registered successfully.")
    @RolesAllowed("ROLE_ASSOCIATE")
    @PostMapping
    public ResponseEntity<Void> registerVote(@RequestBody @Valid VoteRequestDto voteRequestDto) {
        voteService.registerVote(voteRequestDto.agendaId(), voteRequestDto.voteOption());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}