package com.votify.repositories;

import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    Page<UserModel> findByActiveTrue(Pageable pageable);

    Page<UserModel> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<UserModel> findByActiveTrueAndRole(UserRole role, Pageable pageable);

    Page<UserModel> findByActiveTrueAndNameContainingIgnoreCaseAndRole(String name, UserRole role, Pageable pageable);

    Optional<UserModel> findByIdAndRole(Long id, UserRole role);
}
