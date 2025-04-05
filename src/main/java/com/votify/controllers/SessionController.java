package com.votify.controllers;

import com.votify.dtos.ApiResponseDto;
import com.votify.dtos.SessionDto;
import com.votify.dtos.SessionRequestPutDTO;
import com.votify.enums.SortSession;
import com.votify.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Create a new session", description = "Create a new session", responses = {
            @ApiResponse(responseCode = "201", description = "Session created"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Invalid data, missing title or description\"}")
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
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid SessionDto sessionDto,
            BindingResult bindingResult
    ) {
        this.sessionService.save(sessionDto, bindingResult);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Get all sessions", description = "Get all sessions", responses = {
            @ApiResponse(responseCode = "200", description = "List of sessions"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                    ))
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<SessionDto>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam SortSession sort,
            @RequestParam Sort.Direction direction
    ) {
        ApiResponseDto<SessionDto> sessions = sessionService.findAll(page, sort, direction);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @Operation(summary = "Get a session by id", description = "Get a session by id", responses = {
            @ApiResponse(responseCode = "200", description = "Session found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                    )),
            @ApiResponse(responseCode = "404", description = "Session not found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "SessionNotFound", value = "{\"message\": \"Session not found\"}")
                            })
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getById(@PathVariable Long id) {
        SessionDto session = sessionService.findById(id);
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @Operation(summary = "Update part of a session", description = "Update a session", responses = {
            @ApiResponse(responseCode = "200", description = "Session updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Invalid data, missing title or description\"}")
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
            @ApiResponse(responseCode = "404", description = "Session not found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "SessionNotFound", value = "{\"message\": \"Session not found\"}")
                            })
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> update(
            @PathVariable Long id,
            @RequestBody @Valid SessionRequestPutDTO sessionRequestPutDTO,
            BindingResult bindingResult
    ) {
        SessionDto updatedSession = this.sessionService.update(id, sessionRequestPutDTO, bindingResult);
        return ResponseEntity.status(200).body(updatedSession);
    }

    @Operation(summary = "Delete a session", description = "Delete a sessionon", responses = {
            @ApiResponse(responseCode = "204", description = "Session deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "UnauthorizedAccess", value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
                    )),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "AccessDenied", value = "{\"message\": \"You do not have permission to access this resource\"}")
                    )),
            @ApiResponse(responseCode = "404", description = "Session not found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "SessionNotFound", value = "{\"message\": \"Session not found\"}")
                            })
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.sessionService.delete(id);
        return ResponseEntity.status(204).build();
    }
}