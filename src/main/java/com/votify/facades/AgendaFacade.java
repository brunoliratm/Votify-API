package com.votify.facades;

import com.votify.dtos.requests.AgendaRequestDto;
import com.votify.dtos.requests.AgendaRequestPutDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.AgendaUniqueResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.enums.SortAgenda;
import com.votify.services.AgendaService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class AgendaFacade {
    private final AgendaService agendaService;
    
    public AgendaFacade(AgendaService agendaService) {
        this.agendaService = agendaService;
    }
    
    public void create(AgendaRequestDto agendaRequestDto, BindingResult bindingResult) {
        this.agendaService.save(agendaRequestDto, bindingResult);
    }
    
    public ApiResponseDto<AgendaResponseDto> getAll(int page, SortAgenda sort, Sort.Direction direction) {
        return this.agendaService.findAll(page, sort, direction);
    }
    
    public AgendaUniqueResponseDto getById(Long id) {
        return this.agendaService.findById(id);
    }
    
    public void update(Long id, AgendaRequestPutDto agendaRequestPutDto, BindingResult bindingResult) {
        this.agendaService.update(id, agendaRequestPutDto, bindingResult);
    }
    
    public void delete(Long id) {
        this.agendaService.delete(id);
    }
    
    public void startVoting(Long id, Integer durationSeconds) {
        this.agendaService.startVoting(id, durationSeconds);
    }
    
}
