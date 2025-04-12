package com.votify.dtos.requests;

import com.votify.enums.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VoteRequestDto (
    @NotNull(message = "Agenda ID cannot be null.")
    @Positive(message = "Agenda ID must be a positive number.")
    @Schema(description = "ID of the agenda to vote on", example = "1")
    Long agendaId,
    @NotNull(message = "Vote option cannot be null.")
    @Schema(description = "Vote option (YES or NO)", example = "YES")
    VoteOption voteOption
) {
    
}
