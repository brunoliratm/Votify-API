package com.votify.repositories;

import com.votify.enums.VoteOption;
import com.votify.models.VoteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<VoteModel, Long> {

    @Query("SELECT COUNT(v) > 0 FROM VoteModel v WHERE v.associateId.id = :associateId AND v.agenda.id = :agendaId")
    boolean hasAssociateVoted(@Param("associateId") Long associateId, @Param("agendaId") Long agendaId);
    
    @Query("SELECT COUNT(v) FROM VoteModel v WHERE v.agenda.id = :agendaId AND v.voteType = :voteType")
    long countVotesByType(@Param("agendaId") Long agendaId, @Param("voteType") VoteOption voteType);
}
