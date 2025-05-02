package com.votify.services;

import com.votify.dtos.*;
import com.votify.dtos.requests.AgendaRequestDto;
import com.votify.dtos.requests.AgendaRequestPutDto;
import com.votify.dtos.responses.AgendaResponseDto;
import com.votify.dtos.responses.AgendaUniqueResponseDto;
import com.votify.dtos.responses.ApiResponseDto;
import com.votify.dtos.responses.VoteResponseDto;
import com.votify.dtos.responses.infoVotesResponseDto;
import com.votify.enums.AgendaStatus;
import com.votify.enums.SortAgenda;
import com.votify.enums.VoteOption;
import com.votify.exceptions.AgendaNotFoundException;
import com.votify.exceptions.PageNotFoundException;
import com.votify.exceptions.SessionNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.exceptions.VotingException;
import com.votify.helpers.UtilHelper;
import com.votify.interfaces.IAgendaService;
import com.votify.models.AgendaModel;
import com.votify.models.SessionModel;
import com.votify.repositories.AgendaRepository;
import com.votify.repositories.SessionRepository;
import com.votify.repositories.VoteRepository;
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
import java.util.stream.Collectors;

@Service
public class AgendaService implements IAgendaService {
    private final AgendaRepository agendaRepository;
    private final UtilHelper utilHelper;
    private final SessionRepository sessionRepository;
    private final VoteRepository voteRepository;

    public AgendaService(AgendaRepository agendaRepository,
                         UtilHelper utilHelper,
                         SessionRepository sessionRepository,
                         VoteRepository voteRepository
    ) {
        this.agendaRepository = agendaRepository;
        this.utilHelper = utilHelper;
        this.sessionRepository = sessionRepository;
        this.voteRepository = voteRepository;
    }

    @Override
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

    @Override
    public ApiResponseDto<AgendaResponseDto> findAll(int page, SortAgenda sort, Sort.Direction sortDirection) {
        int pageIndex = (page > 0) ? (page - 1) : 0;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Page<AgendaResponseDto> responsePage = this.agendaRepository.findAllActive(pageable)
                .map(this::convertAgendaToDto);

        if (pageIndex > responsePage.getTotalPages()) {
            throw new PageNotFoundException(responsePage.getTotalPages());
        }

        InfoDto infoDto = this.utilHelper.buildPageableInfoDto(responsePage, "agendas");

        return new ApiResponseDto<>(infoDto, responsePage.getContent());
    }

    public AgendaModel getAgendaById(Long id) {
        return this.agendaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(AgendaNotFoundException::new);
    }

    public AgendaUniqueResponseDto findById(Long id) {
        AgendaModel agenda = this.getAgendaById(id);
        return convertAgendaUniqueToDto(agenda);
    }

    private AgendaUniqueResponseDto convertAgendaUniqueToDto(AgendaModel agenda) {
        Long approvals = voteRepository.countVotesByType(agenda.getId(), VoteOption.YES);
        Long disapprovals = voteRepository.countVotesByType(agenda.getId(), VoteOption.NO);
        Long totalVotes = approvals + disapprovals;

        infoVotesResponseDto infoVotes = new infoVotesResponseDto(
            totalVotes,
            approvals,
            disapprovals
        );

        return new AgendaUniqueResponseDto(
            agenda.getId(),
            agenda.getTitle(),
            agenda.getDescription(),
            agenda.getSession().getId(),
            agenda.getStatus().toString(),
            agenda.getStartVotingAt(),
            agenda.getEndVotingAt(),
            infoVotes,
            agenda.getVotes().stream()
                .map(vote -> new VoteResponseDto(
                    vote.getId(),
                    vote.getAssociateId().getId(),
                    vote.getAssociateId().getName(),
                    vote.getVoteType().toString(),
                    vote.getVotedAt()
                )).collect(Collectors.toList())
        );
    }

    @Transactional
    public void update(Long id, AgendaRequestPutDto agendaRequestPutDto, BindingResult bindingResult) {
        validateErrorFields(bindingResult);
        AgendaModel agenda = this.getAgendaById(id);
        boolean isUpdated = false;

        if (agendaRequestPutDto.title() != null && !agendaRequestPutDto.title().equals(agenda.getTitle())) {
            agenda.setTitle(agendaRequestPutDto.title());
            isUpdated = true;
        }

        if (agendaRequestPutDto.description() != null && !agendaRequestPutDto.description().equals(agenda.getDescription())) {
            agenda.setDescription(agendaRequestPutDto.description());
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

    public void startVoting(Long id, Integer durationSeconds){
        AgendaModel agenda = getAgendaById(id);

        if (agenda.getStatus() == AgendaStatus.OPEN) {
            throw new VotingException("Voting is already open for this agenda");
        }

        if (agenda.getStatus() == AgendaStatus.CLOSED ||
            (agenda.getEndVotingAt() != null && agenda.getEndVotingAt().isBefore(LocalDateTime.now()))) {
            throw new VotingException("Voting is closed for this agenda");
        }

        agenda.setStatus(AgendaStatus.OPEN);
        agenda.setStartVotingAt(LocalDateTime.now());
        agenda.setEndVotingAt(LocalDateTime.now().plusSeconds(durationSeconds != null ? durationSeconds : 60));
        agendaRepository.save(agenda);
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
        Long approvals= voteRepository.countVotesByType(agendaModel.getId(), VoteOption.YES);
        Long disapprovals= voteRepository.countVotesByType(agendaModel.getId(), VoteOption.NO);
        Long totalVotes = approvals + disapprovals;

        infoVotesResponseDto infoVotes = new infoVotesResponseDto(
            totalVotes,
            approvals,
            disapprovals
        );

        return new AgendaResponseDto(
                agendaModel.getId(),
                agendaModel.getTitle(),
                agendaModel.getDescription(),
                agendaModel.getSession().getId(),
                agendaModel.getStatus().toString(),
                agendaModel.getStartVotingAt(),
                agendaModel.getEndVotingAt(),
                infoVotes
        );
    }

    @Transactional
    public boolean validateVotingAvailability(AgendaModel agenda) {
        if (!agenda.isVotingAvailable()) {
            if (agenda.getStatus() == AgendaStatus.OPEN && agenda.hasVotingEnded()) {
                agenda.setStatus(AgendaStatus.CLOSED);
                agendaRepository.save(agenda);
            }
            return false;
        }

        return true;
    }
}


