package com.votify.services;

import com.votify.dtos.SessionDto;
import com.votify.exceptions.ValidationErrorException;
import com.votify.models.SessionModel;
import com.votify.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public void save(SessionDto sessionDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);

        LocalDateTime startDate = (sessionDto.startDate() != null)
                    ? sessionDto.startDate()
                    : LocalDateTime.now();

        SessionModel session = new SessionModel();
        session.setTitle(sessionDto.title());
        session.setDescription(sessionDto.description());
        session.setStartDate(startDate);
        session.setEndDate(sessionDto.endDate());
        sessionRepository.save(session);
    }

    public List<SessionDto> findAll() {
        return sessionRepository.findAll().stream()
                .map(this::convertSessionToDto)
                .collect(Collectors.toList());
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
                sessionModel.getEndDate()
        );
    }
}
