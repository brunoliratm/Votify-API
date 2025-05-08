package com.votify.facades;

import com.votify.dtos.requests.VoteRequestDto;
import com.votify.interfaces.IVoteService;
import org.springframework.stereotype.Component;

@Component
public class VoteFacade {
  private final IVoteService voteService;

  public VoteFacade(IVoteService voteService) {
    this.voteService = voteService;
  }

  public void registerVote(VoteRequestDto voteRequestDto) {
    this.voteService.registerVote(voteRequestDto);
  }

  public void updateVote(VoteRequestDto voteRequestDto) {
    this.voteService.updateVote(voteRequestDto);
  }
}
