package com.votify.repositories;

import com.votify.enums.AgendaStatus;
import com.votify.models.AgendaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendaRepository extends JpaRepository<AgendaModel, Long> {
    @Query("SELECT a FROM AgendaModel a WHERE a.deletedAt IS NULL")
    Page<AgendaModel> findAllActive(Pageable pageable);

    @Query("SELECT s FROM AgendaModel s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<AgendaModel> findByIdActive(Long id);

    List<AgendaModel> findByStatusAndEndVotingAtBefore(AgendaStatus status, LocalDateTime endTime);
}
