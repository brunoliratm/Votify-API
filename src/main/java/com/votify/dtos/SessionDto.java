package com.votify.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record SessionDto(
        @NotBlank(message = "{sessions.title.notBlank}")
        String title,

        @NotBlank(message = "{sessions.description.notBlank}")
        String description,

        @JsonProperty("start_date")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @JsonProperty("end_date")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate
) {
}