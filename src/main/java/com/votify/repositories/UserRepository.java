package com.votify.repositories;

import com.votify.enums.UserRole;
import com.votify.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByIdAndRole(Long id, UserRole role);
}
