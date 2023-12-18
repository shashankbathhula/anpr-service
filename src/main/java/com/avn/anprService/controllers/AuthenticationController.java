package com.avn.anprService.controllers;

import com.avn.anprService.dto.GenericResponse;
import com.avn.anprService.dto.JwtAuthenticationResponse;
import com.avn.anprService.dto.SignInRequest;
import com.avn.anprService.dto.SignUpRequest;
import com.avn.anprService.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public GenericResponse<String> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/login")
    public GenericResponse<JwtAuthenticationResponse> login(@RequestBody SignInRequest request) {
        try {
            return GenericResponse.success("Success", authenticationService.login(request));
        } catch (Exception e) {
            logger.error("Exception while USER Login ::: {}", e.getMessage());
            return GenericResponse.error(e.getMessage(), 400);
        }
    }

    @PostMapping("activateUser")
    public GenericResponse<String> processConfirmationForm(@Validated @RequestBody SignUpRequest signUpRequest) {
        return authenticationService.activateUser(signUpRequest);

    }

    @PostMapping("/forgot")
    public GenericResponse<String> forgotPassword(@RequestBody String email) {
        return authenticationService.forgotPassword(email);
    }

    @PostMapping("/reset/{email}/{token}")
    public GenericResponse<String> changePassword(@PathVariable(value = "email") String email,
                                              @PathVariable(value = "token") String token,
                                              @RequestBody String password) {
       return authenticationService.changePassword(email, token, password);
    }
}