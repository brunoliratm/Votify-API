package com.votify.repositories;

import com.votify.models.VoteModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteModel, Long> {
}
