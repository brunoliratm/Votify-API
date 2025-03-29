package com.votify.controllers;

import com.votify.dtos.requests.AgendaRequestDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.enums.SortAgenda;
import com.votify.services.AgendaService;
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
@RequestMapping("/api/${api.version}/agendas")
public class AgendaController {
    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Operation(summary = "Create a new agenda", description = "Create a new agenda", responses = {
            @ApiResponse(responseCode = "201", description = "Agenda created"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Validation error\", \"errors\": [\"Agenda title can't be null\"]}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid AgendaRequestDto agendaRequestDto,
           BindingResult bindingResult
    ) {
        this.agendaService.save(agendaRequestDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all agendas", description = "Get all agendas", responses = {
        @ApiResponse(responseCode = "200", description = "List of agendas"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"An unknown error occurred\"}")
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<AgendaResponseDto>> getAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "id") SortAgenda sort,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction
    ) {
        ApiResponseDto<AgendaResponseDto> response = this.agendaService.findAll(page, sort, direction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get an agenda by id", description = "Get an agenda by id", responses = {
        @ApiResponse(responseCode = "200", description = "Agenda found"),
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
    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponseDto> getById(@PathVariable Long id) {
        AgendaResponseDto response = this.agendaService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update an agenda", description = "Update an agenda", responses = {
        @ApiResponse(responseCode = "200", description = "Agenda updated"),
        @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"Validation error\", \"errors\": [\"Agenda title can't be null\"]}")
            )
        ),
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
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody @Valid AgendaRequestDto agendaRequestDto,
            BindingResult bindingResult
    ) {
        this.agendaService.update(id, agendaRequestDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete an agenda", description = "Delete an agenda", responses = {
        @ApiResponse(responseCode = "204", description = "Agenda deleted"),
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.agendaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

