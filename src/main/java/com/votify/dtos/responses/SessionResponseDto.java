package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record SessionResponseDto(
        Long id,
        String title,
        String description,
        @JsonProperty("start_date") LocalDateTime startDate,
        @JsonProperty("end_date") LocalDateTime endDate,
        SessionUserDto organizer,
        List<AgendaResponseDto> agendas
) {
}
