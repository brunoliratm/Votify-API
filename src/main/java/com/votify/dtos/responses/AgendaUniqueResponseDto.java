package com.votify.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgendaUniqueResponseDto(
    Long id,
    String title,
    String description,
    Long sessionId,
    String status,
    LocalDateTime startVotingAt,
    LocalDateTime endVotingAt,
    infoVotesResponseDto informationVotes,
    List<VoteResponseDto> votes
) {

}
