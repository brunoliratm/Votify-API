package com.votify.repositories;

import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    @Query("""
            SELECT u FROM UserModel u
            WHERE (:name IS NULL OR LOWER(CAST(u.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
            AND (:role IS NULL OR u.role = :role)
            AND u.deletedAt IS NULL
        """)
    Page<UserModel> findByFilters(@Param("name") String name, @Param("role") UserRole role, Pageable pageable);
}