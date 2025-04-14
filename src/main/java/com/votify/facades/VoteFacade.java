package com.votify.facades;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.services.VoteService;
import org.springframework.stereotype.Component;

@Component
public class VoteFacade {
    private final VoteService voteService;

    public VoteFacade(VoteService voteService) {
        this.voteService = voteService;
    }

    public void registerVote(VoteRequestDto voteRequestDto) {
        this.voteService.registerVote(voteRequestDto);
    }
}
