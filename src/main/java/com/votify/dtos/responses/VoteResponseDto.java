package com.votify.dtos.responses;

import java.time.LocalDateTime;

public record VoteResponseDto (
        Long id,
        Long associateId,
        String voteType,
        LocalDateTime votedAt
){

}
