package com.votify.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponseDto<T>(
    InfoDto info,
    List<T> results
) {
}