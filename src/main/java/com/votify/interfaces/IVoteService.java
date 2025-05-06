package com.votify.interfaces;

import com.votify.dtos.requests.VoteRequestDto;
import org.springframework.transaction.annotation.Transactional;

public interface IVoteService {

  @Transactional
  void registerVote(VoteRequestDto voteRequestDto);

  @Transactional
  void updateVote(VoteRequestDto voteRequestDto);
}
