package com.votify.services;

import com.votify.dtos.ApiResponseDto;
import com.votify.dtos.InfoDto;
import com.votify.dtos.SessionDto;
import com.votify.enums.SortSession;
import com.votify.exceptions.SessionNotFoundException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.helpers.ConvertHelper;
import com.votify.models.SessionModel;
import com.votify.models.UserModel;
import com.votify.repositories.SessionRepository;
import com.votify.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ConvertHelper convertHelper;

    public SessionService(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            ConvertHelper convertHelper
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.convertHelper = convertHelper;
    }

    public void save(SessionDto sessionDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);

        LocalDateTime startDate = (sessionDto.startDate() != null)
                    ? sessionDto.startDate()
                    : LocalDateTime.now();

        UserModel organizer = userRepository.findById(sessionDto.organizerId())
                .orElseThrow(UserNotFoundException::new);

        SessionModel session = new SessionModel();
        session.setTitle(sessionDto.title());
        session.setDescription(sessionDto.description());
        session.setStartDate(startDate);
        session.setEndDate(sessionDto.endDate());
        session.setOrganizer(organizer);
        sessionRepository.save(session);
    }

    public ApiResponseDto<SessionDto> findAll(int page, SortSession sort, Sort.Direction sortDirection) {
        int pageIndex = (page > 0) ? (page - 1) : 0;
        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Page<SessionModel> sessionPage = sessionRepository.findAll(pageable);
        Page<SessionDto> responsePage = sessionPage.map(this::convertSessionToDto);

        InfoDto infoDto = convertHelper.buildPageableInfoDto(responsePage, "sessions");

        List<SessionDto> results = sessionPage.getContent().stream()
                .map(this::convertSessionToDto)
                .toList();

        return new ApiResponseDto<>(infoDto, results);
    }

    public SessionDto findById(Long id) {
        SessionModel sessionModel = sessionRepository.findById(id)
                .orElseThrow(SessionNotFoundException::new);

        return convertSessionToDto(sessionModel);
    }

    public SessionDto update(Long id, SessionDto sessionDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);

        SessionModel session = sessionRepository.findById(id)
                .orElseThrow(SessionNotFoundException::new);

        LocalDateTime startDate = (sessionDto.startDate() != null)
                ? sessionDto.startDate()
                : LocalDateTime.now();

        session.setTitle(sessionDto.title());
        session.setDescription(sessionDto.description());
        session.setStartDate(startDate);
        session.setEndDate(sessionDto.endDate());
        sessionRepository.save(session);

        return convertSessionToDto(session);
    }

    private void validateErrorFields(BindingResult bindingResult) {
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
}
