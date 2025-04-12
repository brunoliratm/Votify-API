package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record infoVotesResponseDto(
        Long totalVotes,
        Long totalVotesYes,
        Long totalVotesNo
) {

}
