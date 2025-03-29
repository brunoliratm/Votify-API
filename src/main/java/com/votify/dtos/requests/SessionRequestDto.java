package com.votify.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionRequestDto(
    @NotNull(message = "{sessions.title.notNull}")
    @NotBlank(message = "{sessions.title.notBlank}")
    String title,

    @NotNull(message = "{sessions.description.notNull}")
    @NotBlank(message = "{sessions.description.notBlank}")
    String description,

    @NotNull(message = "{sessions.startDate.notNull}")
    @FutureOrPresent(message = "{sessions.startDate.futureOrPresent}")
    @JsonProperty("start_date")
    LocalDateTime startDate,

    @FutureOrPresent(message = "{sessions.endDate.futureOrPresent}")
    @JsonProperty("end_date")
    LocalDateTime endDate,

    @NotNull(message = "{sessions.organizerId.notNull}")
    @JsonProperty("organizer_id")
    Long organizerId
) {
}