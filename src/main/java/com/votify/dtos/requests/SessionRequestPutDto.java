package com.votify.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.votify.interfaces.SessionDateInterval;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionRequestPutDto(
    @NotNull(message = "{sessions.title.notNull}")
    @NotBlank(message = "{sessions.title.notBlank}")
    String title,

    @NotNull(message = "{sessions.description.notNull}")
    @NotBlank(message = "{sessions.description.notBlank}")
    String description,

    @NotNull(message = "{sessions.startDate.notNull}")
    @FutureOrPresent(message = "{sessions.startDate.futureOrPresent}")
    @JsonProperty("start_date")
    @Schema(type = "string", format = "date-time", example = "2025-04-07T10:00:00")
    LocalDateTime startDate,

    @NotNull(message = "{sessions.endDate.notNull}")
    @FutureOrPresent(message = "{sessions.endDate.futureOrPresent}")
    @JsonProperty("end_date")
    @Schema(type = "string", format = "date-time", example = "2025-04-07T10:05:00")
    LocalDateTime endDate
) implements SessionDateInterval {
}