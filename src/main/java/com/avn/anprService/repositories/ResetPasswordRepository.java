package com.avn.anprService.repositories;

import com.avn.anprService.models.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    Optional<ResetPassword> findByTokenAndEmail(String token, String email);
    ResetPassword findByEmail(String email);
}
