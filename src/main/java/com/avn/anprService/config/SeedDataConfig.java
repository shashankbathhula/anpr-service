package com.avn.anprService.config;

import com.avn.anprService.models.Role;
import com.avn.anprService.models.User;
import com.avn.anprService.repositories.UserRepository;
import com.avn.anprService.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        
      if (userRepository.count() == 0) {

        User admin = User
                      .builder()
                      .firstName("admin")
                      .lastName("admin")
                      .email("admin@admin.com")
                      .password(passwordEncoder.encode("password"))
                      .role(Role.ROLE_ADMIN)
                      .build();

        userService.save(admin);
        log.debug("created ADMIN user - {}", admin);
      }
    }

}
