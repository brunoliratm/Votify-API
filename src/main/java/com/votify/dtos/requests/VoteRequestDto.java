package com.votify.dtos.requests;

import com.votify.enums.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VoteRequestDto (
    @NotNull(message = "{agendas.sessionId.NotNull}")
    @Positive(message = "Agenda ID must be a positive number.")
    @Schema(description = "ID of the agenda to vote on", example = "1")
    Long agendaId,
    @NotNull(message = "{vote.option.NotNull}")
    VoteOption voteOption
) {
    
}
