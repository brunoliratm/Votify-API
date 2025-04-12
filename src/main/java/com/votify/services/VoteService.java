package com.votify.services;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.enums.AgendaStatus;
import com.votify.enums.VoteOption;
import com.votify.exceptions.AgendaNotFoundException;
import com.votify.exceptions.VotingException;
import com.votify.models.AgendaModel;
import com.votify.models.UserModel;
import com.votify.models.VoteModel;
import com.votify.repositories.AgendaRepository;
import com.votify.repositories.VoteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final AgendaRepository agendaRepository;

    public VoteService(VoteRepository voteRepository, AgendaRepository agendaRepository) {
        this.voteRepository = voteRepository;
        this.agendaRepository = agendaRepository;
    }

    @Transactional
    public void registerVote(VoteRequestDto voteRequestDto) {
        Long agendaId = voteRequestDto.agendaId();
        VoteOption voteOption = voteRequestDto.voteOption();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = (UserModel) authentication.getPrincipal();

        Long associateId = currentUser.getId();
        AgendaModel agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException());

        if (agenda.getStatus() != AgendaStatus.OPEN || 
            LocalDateTime.now().isBefore(agenda.getStartVotingAt()) || 
            LocalDateTime.now().isAfter(agenda.getEndVotingAt())) {
            throw new VotingException("Voting is not allowed for this agenda at this time");
        }

        if (voteRepository.hasAssociateVoted(associateId, agendaId)) {
            throw new VotingException("Associate has already voted on this agenda");
        }

        VoteModel vote = new VoteModel();
        vote.setAssociateId(associateId);
        vote.setAgenda(agenda);
        vote.setVoteType(voteOption);
        vote.setVotedAt(LocalDateTime.now()); 

        voteRepository.save(vote);
    }

}