package com.votify.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;

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
