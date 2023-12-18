package com.avn.anprService.repositories;

import java.util.List;
import java.util.Optional;

import com.avn.anprService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  User findUserByEmail(String email);

  List<User> findByIdIn(List<Long> userIds);

  Boolean existsByEmail(String email);

  User findByConfirmationToken(String confirmationToken);
  
}
