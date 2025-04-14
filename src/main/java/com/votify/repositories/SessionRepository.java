package com.votify.repositories;

import com.votify.models.SessionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionModel, Long> {
    @Query("SELECT s FROM SessionModel s WHERE s.deletedAt IS NULL")
    Page<SessionModel> findAllActive(Pageable pageable);

    @Query("SELECT s FROM SessionModel s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<SessionModel> findByIdActive(Long id);
}
