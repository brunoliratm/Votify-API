package com.votify.repositories;

import com.votify.models.UserModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);
    
    Page<UserModel> findByActiveTrue(Pageable pageable);
}
