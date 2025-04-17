package com.votify.interfaces;

import org.springframework.validation.BindingResult;
import com.votify.dtos.requests.AgendaRequestDto;
import com.votify.dtos.requests.AgendaRequestPutDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.AgendaUniqueResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.enums.SortAgenda;
import org.springframework.data.domain.Sort;


public interface IAgendaService {

    void save(AgendaRequestDto agendaRequestDto, BindingResult bindingResult);

    ApiResponseDto<AgendaResponseDto> findAll(int page, SortAgenda sort, Sort.Direction direction);

    AgendaUniqueResponseDto findById(Long id);

    void update(Long id, AgendaRequestPutDto agendaRequestPutDto, BindingResult bindingResult);

    void delete(Long id);

    void startVoting(Long id, Integer durationSeconds);

}
