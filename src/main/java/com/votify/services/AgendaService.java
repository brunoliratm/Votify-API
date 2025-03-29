package com.votify.services;

import com.votify.dtos.*;
import com.votify.dtos.requests.AgendaRequestDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.enums.SortAgenda;
import com.votify.exceptions.AgendaNotFoundException;
import com.votify.exceptions.PageNotFoundException;
import com.votify.exceptions.SessionNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.helpers.UtilHelper;
import com.votify.models.AgendaModel;
import com.votify.models.SessionModel;
import com.votify.repositories.AgendaRepository;
import com.votify.repositories.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {
    private final AgendaRepository agendaRepository;
    private final UtilHelper utilHelper;
    private final SessionRepository sessionRepository;

    public AgendaService(AgendaRepository agendaRepository,
                         UtilHelper utilHelper,
                         SessionRepository sessionRepository
    ) {
        this.agendaRepository = agendaRepository;
        this.utilHelper = utilHelper;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void save(AgendaRequestDto agendaRequestDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);
        SessionModel session = this.sessionRepository.findByIdActive(agendaRequestDto.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        AgendaModel agenda = new AgendaModel();
        agenda.setTitle(agendaRequestDto.title());
        agenda.setDescription(agendaRequestDto.description());
        agenda.setSession(session);
        this.agendaRepository.save(agenda);
    }

    public ApiResponseDto<AgendaResponseDto> findAll(int page, SortAgenda sort, Sort.Direction sortDirection) {
        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Page<AgendaResponseDto> responsePage = this.agendaRepository.findAllActive(pageable)
                .map(this::convertAgendaToDto);

        if (pageIndex >= responsePage.getTotalPages()) {
            throw new PageNotFoundException(responsePage.getTotalPages());
        }

        InfoDto infoDto = this.utilHelper.buildPageableInfoDto(responsePage, "agendas");

        return new ApiResponseDto<>(infoDto, responsePage.getContent());
    }

    public AgendaModel getAgendaById(Long id) {
        return this.agendaRepository.findByIdActive(id)
                .orElseThrow(AgendaNotFoundException::new);
    }

    public AgendaResponseDto findById(Long id) {
        AgendaModel agenda = this.getAgendaById(id);
        return convertAgendaToDto(agenda);
    }

    @Transactional
    public void update(Long id, AgendaRequestDto agendaRequestDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);
        AgendaModel agenda = this.getAgendaById(id);
        boolean isUpdated = false;

        if (agendaRequestDto.title() != null && !agendaRequestDto.title().equals(agenda.getTitle())) {
            agenda.setTitle(agendaRequestDto.title());
            isUpdated = true;
        }

        if (agendaRequestDto.description() != null && !agendaRequestDto.description().equals(agenda.getDescription())) {
            agenda.setDescription(agendaRequestDto.description());
            isUpdated = true;
        }

        if (isUpdated) {
            this.agendaRepository.save(agenda);
        }
    }

    @Transactional
    public void delete(Long id) {
        AgendaModel agenda = this.getAgendaById(id);
        agenda.delete();
        this.agendaRepository.save(agenda);
    }

    private void validateErrorFields(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    public AgendaResponseDto convertAgendaToDto(AgendaModel agendaModel) {
        return new AgendaResponseDto(
                agendaModel.getId(),
                agendaModel.getTitle(),
                agendaModel.getDescription(),
                agendaModel.getVotes()
        );
    }
}
