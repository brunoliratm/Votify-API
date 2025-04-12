package com.votify.controllers;

import com.votify.enums.VoteOption;
import com.votify.services.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
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

    @Operation(summary = "Register a vote", description = "Allows the currently logged-in associate to vote on an agenda.")
    @ApiResponse(responseCode = "201", description = "Vote registered successfully.")
    @RolesAllowed("ROLE_ASSOCIATE")
    @PostMapping
    public ResponseEntity<Void> registerVote(@RequestParam @NotNull Long agendaId,
                                             @RequestParam @NotNull VoteOption voteOption) {
        voteService.registerVote(agendaId, voteOption);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}