package com.votify.repositories;

import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    Page<UserModel> findByDeletedAtIsNull(Pageable pageable);

    Page<UserModel> findByDeletedAtIsNullAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<UserModel> findByDeletedAtIsNullAndRole(UserRole role, Pageable pageable);

    Page<UserModel> findByDeletedAtIsNullAndNameContainingIgnoreCaseAndRole(String name, UserRole role, Pageable pageable);
}