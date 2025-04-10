package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.votify.models.VoteModel;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AgendaResponseDto(
        Long id,
        String title,
        String description,
        List<VoteModel> votes
) {
}
