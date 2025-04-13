package com.votify.services;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.enums.UserRole;
import com.votify.enums.VoteOption;
import com.votify.exceptions.VotingException;
import com.votify.facades.AgendaFacade;
import com.votify.models.AgendaModel;
import com.votify.models.UserModel;
import com.votify.models.VoteModel;
import com.votify.repositories.VoteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final AgendaFacade agendaFacade;

    public VoteService(VoteRepository voteRepository, AgendaFacade agendaFacade) {
        this.voteRepository = voteRepository;
        this.agendaFacade = agendaFacade;
    }

    @Transactional
    public void registerVote(VoteRequestDto voteRequestDto) {
        Long agendaId = voteRequestDto.agendaId();
        VoteOption voteOption = voteRequestDto.voteOption();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = (UserModel) authentication.getPrincipal();

        AgendaModel agenda = this.agendaFacade.getAgendaById(agendaId);

        if (!this.agendaFacade.validateVotingAvailability(agenda)) {
            throw new VotingException("Voting is not allowed for this agenda at this time");
        } else if (this.voteRepository.hasAssociateVoted(currentUser.getId(), agendaId)) {
            throw new VotingException("You has already voted on this agenda");
        } else if (!currentUser.getRole().equals(UserRole.ASSOCIATE)) {
            throw new VotingException("Only associates can vote");
        }

        VoteModel vote = new VoteModel();
        vote.setAssociateId(currentUser);
        vote.setAgenda(agenda);
        vote.setVoteType(voteOption);
        vote.setVotedAt(LocalDateTime.now());

        this.voteRepository.save(vote);
    }

}
