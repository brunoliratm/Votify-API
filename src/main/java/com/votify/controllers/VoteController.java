package com.votify.controllers;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.facades.VoteFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/${api.version}/votes")
public class VoteController {

    private final VoteFacade voteFacade;

    public VoteController(VoteFacade voteFacade) {
        this.voteFacade = voteFacade;
    }

    @Operation(summary = "Register a vote", description = "Allows the currently logged-in associate to vote on an agenda.", responses = {
            @ApiResponse(responseCode = "201", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                @ExampleObject(name = "ValidationError", value = "{\"message\": \"Validation error\", \"errors\": [\"Vote option cannot be null\"]}"),
                                @ExampleObject(name = "VotingNotAllowed", value = "{\"message\": \"Voting is not allowed for this agenda at this time\"}"),
                                @ExampleObject(name = "AlreadyVoted", value = "{\"message\": \"You has already voted on this agenda\"}"),
                                @ExampleObject(name = "OnlyAssociates", value = "{\"message\": \"Only associates can vote\"}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                    )),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                    )),
            @ApiResponse(responseCode = "404", description = "Agenda not found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "AgendaNotFound", value = "{\"message\": \"Agenda not found\"}")
                            })
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> registerVote(@RequestBody @Valid VoteRequestDto voteRequestDto) {
        this.voteFacade.registerVote(voteRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}