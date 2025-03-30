package com.votify.services;

import com.votify.dto.ApiResponseDto;
import com.votify.dto.InfoDto;
import com.votify.dto.SessionDto;
import com.votify.dto.SessionRequestPutDTO;
import com.votify.enums.SortSession;
import com.votify.exceptions.*;
import com.votify.helpers.UtilHelper;
import com.votify.models.SessionModel;
import com.votify.models.UserModel;
import com.votify.repositories.SessionRepository;
import com.votify.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final UtilHelper utilHelper;

    public SessionService(
        SessionRepository sessionRepository,
        UserService userService,
        UtilHelper utilHelper
    ) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
        this.utilHelper = utilHelper;
    }

    @Transactional
    public void save(SessionDto sessionDto, BindingResult bindingResult) {
        validateErrorFields(sessionDto, bindingResult);

        LocalDateTime startDate = (sessionDto.startDate() != null)
            ? sessionDto.startDate()
            : LocalDateTime.now();

        Optional<UserModel> organizer = userService.findOrganizer(sessionDto.organizerId());

        if (organizer.isEmpty()) throw new UserNotFoundException();

        SessionModel session = new SessionModel();
        session.setTitle(sessionDto.title());
        session.setDescription(sessionDto.description());
        session.setStartDate(startDate);
        session.setEndDate(sessionDto.endDate());
        session.setOrganizer(organizer.get());
        sessionRepository.save(session);
    }

    public ApiResponseDto<SessionDto> findAll(int page, SortSession sort, Sort.Direction sortDirection) {
        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Page<SessionDto> responsePage = sessionRepository.findAllActive(pageable)
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

    public SessionDto findById(Long id) {
        SessionModel session = getSessionById(id);
        return convertSessionToDto(session);
    }

    @Transactional
    public SessionDto update(Long id, SessionRequestPutDTO sessionRequestPutDTO, BindingResult bindingResult) {
        validateErrorFieldsSessionRequestPut(sessionRequestPutDTO, bindingResult);
        SessionModel session = getSessionById(id);
        boolean isUpdated = false;

        if (sessionRequestPutDTO.title() != null && !sessionRequestPutDTO.title().equals(session.getTitle())) {
            session.setTitle(sessionRequestPutDTO.title());
            isUpdated = true;
        }
        if (sessionRequestPutDTO.description() != null && !sessionRequestPutDTO.description().equals(session.getDescription())) {
            session.setDescription(sessionRequestPutDTO.description());
            isUpdated = true;
        }
        if (sessionRequestPutDTO.startDate() != null && !sessionRequestPutDTO.startDate().equals(session.getStartDate())) {
            session.setStartDate(sessionRequestPutDTO.startDate());
            isUpdated = true;
        }
        if (sessionRequestPutDTO.endDate() != null && !sessionRequestPutDTO.endDate().equals(session.getEndDate())) {
            session.setEndDate(sessionRequestPutDTO.endDate());
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

    private void validateErrorFields(SessionDto sessionDto, BindingResult bindingResult) {
        if (sessionDto != null && sessionDto.startDate() != null && sessionDto.endDate() != null) {
            if (!sessionDto.startDate().isBefore(sessionDto.endDate())) {
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

    private SessionDto convertSessionToDto(SessionModel sessionModel) {
        return new SessionDto(
            sessionModel.getTitle(),
            sessionModel.getDescription(),
            sessionModel.getStartDate(),
            sessionModel.getEndDate(),
            sessionModel.getOrganizer().getId()
        );
    }
    private void validateErrorFieldsSessionRequestPut(SessionRequestPutDTO sessionRequestPutDTO, BindingResult bindingResult) {
        if (sessionRequestPutDTO != null && sessionRequestPutDTO.startDate() != null && sessionRequestPutDTO.endDate() != null) {
            if (!sessionRequestPutDTO.startDate().isBefore(sessionRequestPutDTO.endDate())) {
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
}
