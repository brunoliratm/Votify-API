package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgendaResponseDto(
        Long id,
        String title,
        String description,
        Long sessionId,
        String status,
        LocalDateTime startVotingAt,
        LocalDateTime endVotingAt,
        infoVotesResponseDto informationVotes
) {
}
