package com.votify.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AgendaRequestPutDto(
        @NotNull(message = "{agendas.title.notNull}")
        @NotBlank(message = "{agendas.title.notBlank}")
        String title,

        @NotNull(message = "{agendas.description.notNull}")
        @NotBlank(message = "{agendas.description.notBlank}")
        String description
) {
}
