package com.votify.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoDto(
        long count,
        long pages,
        String next,
        String prev
) {
}
