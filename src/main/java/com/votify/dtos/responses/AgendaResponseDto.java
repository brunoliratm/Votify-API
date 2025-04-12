package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
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
