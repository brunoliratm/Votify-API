package com.votify.facades;

import com.votify.dtos.requests.SessionRequestDto;
import com.votify.dtos.requests.SessionRequestPutDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.SessionResponseDto;
import com.votify.enums.SortSession;
import com.votify.services.SessionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class SessionFacade {
    private final SessionService sessionService;

    public SessionFacade(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void create(SessionRequestDto sessionRequestDto, BindingResult bindingResult) {
        this.sessionService.save(sessionRequestDto, bindingResult);
    }

    public ApiResponseDto<SessionResponseDto> getAll(int page, SortSession sort, Sort.Direction direction) {
        return this.sessionService.findAll(page, sort, direction);
    }

    public SessionResponseDto getById(Long id) {
        return this.sessionService.findById(id);
    }

    public SessionResponseDto update(Long id, SessionRequestPutDto sessionRequestPutDto, BindingResult bindingResult) {
        return this.sessionService.update(id, sessionRequestPutDto, bindingResult);
    }

    public void delete(Long id) {
        this.sessionService.delete(id);
    }

}
