package com.votify.services;

import com.votify.dtos.requests.SessionRequestPutDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.InfoDto;
import com.votify.dtos.requests.SessionRequestDto;
import com.votify.dtos.responses.SessionResponseDto;
import com.votify.dtos.responses.SessionUserDto;
import com.votify.enums.SortSession;
import com.votify.exceptions.*;
import com.votify.helpers.UtilHelper;
import com.votify.interfaces.SessionDateInterval;
import com.votify.models.AgendaModel;
import com.votify.models.SessionModel;
import com.votify.models.UserModel;
import com.votify.repositories.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final UtilHelper utilHelper;
    private final AgendaService agendaService;

    public SessionService(
        SessionRepository sessionRepository,
        UtilHelper utilHelper,
        AgendaService agendaService,
        UserService userService
    ) {
        this.sessionRepository = sessionRepository;
        this.utilHelper = utilHelper;
        this.agendaService = agendaService;
        this.userService = userService;
    }

    @Transactional
    public void save(SessionRequestDto sessionRequestDto, BindingResult bindingResult) {
        validateErrorFields(sessionRequestDto, bindingResult);

        LocalDateTime startDate = (sessionRequestDto.startDate() != null)
            ? sessionRequestDto.startDate()
            : LocalDateTime.now();

        UserModel organizer;

        organizer = userService.findOrganizer(sessionRequestDto.organizerId());

        SessionModel session = new SessionModel();
        session.setTitle(sessionRequestDto.title());
        session.setDescription(sessionRequestDto.description());
        session.setStartDate(startDate);
        session.setEndDate(sessionRequestDto.endDate());
        session.setOrganizer(organizer);
        sessionRepository.save(session);
    }

    public ApiResponseDto<SessionResponseDto> findAll(int page, SortSession sort, Sort.Direction sortDirection) {
        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Page<SessionResponseDto> responsePage = sessionRepository.findAllActive(pageable)
            .map(this::convertSessionToDto);

        if (pageIndex > responsePage.getTotalPages()) {
            throw new PageNotFoundException(responsePage.getTotalPages());
        }

        InfoDto infoDto = utilHelper.buildPageableInfoDto(responsePage, "sessions");

        return new ApiResponseDto<>(infoDto, responsePage.getContent());
    }

    public SessionModel getSessionById(Long id) {
        return sessionRepository.findByIdActive(id)
            .orElseThrow(SessionNotFoundException::new);
    }

    public SessionResponseDto findById(Long id) {
        SessionModel session = getSessionById(id);
        return convertSessionToDto(session);
    }

    @Transactional
    public SessionResponseDto update(Long id, SessionRequestPutDto sessionRequestDto, BindingResult bindingResult) {
        validateErrorFields(sessionRequestDto, bindingResult);
        SessionModel session = getSessionById(id);
        boolean isUpdated = false;

        if (sessionRequestDto.title() != null && !sessionRequestDto.title().equals(session.getTitle())) {
            session.setTitle(sessionRequestDto.title());
            isUpdated = true;
        }
        if (sessionRequestDto.description() != null && !sessionRequestDto.description().equals(session.getDescription())) {
            session.setDescription(sessionRequestDto.description());
            isUpdated = true;
        }
        if (sessionRequestDto.startDate() != null && !sessionRequestDto.startDate().equals(session.getStartDate())) {
            session.setStartDate(sessionRequestDto.startDate());
            isUpdated = true;
        }
        if (sessionRequestDto.endDate() != null && !sessionRequestDto.endDate().equals(session.getEndDate())) {
            session.setEndDate(sessionRequestDto.endDate());
            isUpdated = true;
        }

        if (isUpdated) {
            sessionRepository.save(session);
        }

        return convertSessionToDto(session);
    }

    @Transactional
    public void delete(Long id) {
        SessionModel session = getSessionById(id);
        session.delete();
        sessionRepository.save(session);
    }

    private void validateErrorFields(SessionDateInterval sessionDateInterval, BindingResult bindingResult) {
        if (sessionDateInterval != null && sessionDateInterval.startDate() != null && sessionDateInterval.endDate() != null) {
            if (!sessionDateInterval.startDate().isBefore(sessionDateInterval.endDate())) {
                throw new StartDateBeforeEndDateException();
            }
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    private SessionResponseDto convertSessionToDto(SessionModel sessionModel) {
        UserModel userOrganizer = sessionModel.getOrganizer();
        SessionUserDto organizer = new SessionUserDto(
            userOrganizer.getId(),
            userOrganizer.getName(),
            userOrganizer.getEmail(),
            userOrganizer.getRole()
        );

        List<AgendaResponseDto> agendas = new ArrayList<>();
        for (AgendaModel agenda : sessionModel.getAgendas()) {
            agendas.add(this.agendaService.convertAgendaToDto(agenda));
        }

        return new SessionResponseDto(
            sessionModel.getId(),
            sessionModel.getTitle(),
            sessionModel.getDescription(),
            sessionModel.getStartDate(),
            sessionModel.getEndDate(),
            organizer,
            agendas
        );
    }
}
