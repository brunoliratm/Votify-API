package com.votify.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.votify.dtos.InfoDto;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponseDto<T>(
    InfoDto info,
    List<T> results
) {
}
