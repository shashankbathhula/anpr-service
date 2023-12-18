package com.avn.anprService.controllers;

import com.avn.anprService.dto.GenericResponse;
import com.avn.anprService.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public GenericResponse<?> allUsers() {
        try {
            return GenericResponse.success("", userService.findAll());
        } catch (Exception e) {
            logger.error("Exception in FindAll :::: {} ", e.getMessage());
            return GenericResponse.error("Exception", 500);
        }

    }
}
