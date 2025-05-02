package com.votify.interfaces;

import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import com.votify.dtos.requests.SessionRequestDto;
import com.votify.dtos.requests.SessionRequestPutDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.SessionResponseDto;
import com.votify.enums.SortSession;
import com.votify.models.SessionModel;

public interface ISessionService {
    void save(SessionRequestDto sessionRequestDto, BindingResult bindingResult);
    
    ApiResponseDto<SessionResponseDto> findAll(int page, SortSession sort, Sort.Direction sortDirection);
    
    SessionModel getSessionById(Long id);
    
    SessionResponseDto findById(Long id);
    
    SessionResponseDto update(Long id, SessionRequestPutDto sessionRequestDto, BindingResult bindingResult);
    
    void delete(Long id);

}
