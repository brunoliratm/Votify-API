package com.votify.dtos.responses;

import java.time.LocalDateTime;

public record VoteResponseDto (
        Long id,
        Long associateId,
        String associateName,
        String voteType,
        LocalDateTime votedAt
){

}
