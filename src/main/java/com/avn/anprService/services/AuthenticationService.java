package com.avn.anprService.services;

import com.avn.anprService.dto.*;
import com.avn.anprService.models.ResetPassword;
import com.avn.anprService.models.Role;
import com.avn.anprService.models.User;
import com.avn.anprService.repositories.ResetPasswordRepository;
import com.avn.anprService.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ResetPasswordRepository resetPasswordRepository;
    private final EmailService emailService;

    @Value("${frontend.app.remote.url}")
    private String frontEndAppURL;

    public GenericResponse<String> signup(SignUpRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return GenericResponse.error("Email Address already in use!", 400);
            }

            var user = User
                    .builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .role(Role.ROLE_USER)
                    .enabled(false)
                    .confirmationToken(UUID.randomUUID().toString())
                    .build();

            user = userService.save(user);
            EmailData emailData = new EmailData();
            emailData.setToken(user.getConfirmationToken());
            emailData.setEmail(user.getEmail());
            emailData.setName(user.getFirstName());
            emailData.setSubject("Activate your account for ANPR");
            emailData.setMessageBody("To confirm your e-mail address, please click the link below");
            sendEmail(emailData, false);
            return GenericResponse.success("Email sent", null);
        } catch (Exception e) {
            return GenericResponse.error(e.getMessage(), 400);
        }
    }


    public JwtAuthenticationResponse login(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public GenericResponse<String> activateUser(SignUpRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            User userData = user.get();
            userData.setEnabled(true);
            userData.setPassword(passwordEncoder.encode(request.getPassword()));
            userData.setConfirmationToken("");
            userRepository.save(userData);
            logger.info("Activate User Request {}  ", request);
            return GenericResponse.success("successfully confirmed, please login", null);
        } else {
            logger.error("Error while Activating the Error {} ", request);
            return GenericResponse.error("please confirm token", 400);
        }
    }

    public GenericResponse<String> forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            try {
                ResetPassword resetPassword;
                String token = UUID.randomUUID().toString();
                ResetPassword passwordReset = resetPasswordRepository.findByEmail(email);
                resetPassword = Objects.requireNonNullElseGet(passwordReset, ResetPassword::new);
                resetPassword.setToken(token);
                resetPassword.setEmail(email);
                resetPasswordRepository.save(resetPassword);

                EmailData emailData = new EmailData();
                emailData.setEmail(email);
                emailData.setMessageBody("Reset Password of " + email);
                emailData.setSubject("To reset your password, please click the link below");
                emailData.setToken(token);
                sendEmail(emailData, true);
                return GenericResponse.success("Email sent", null);
            } catch (Exception e) {
                logger.error(e.getCause().getMessage());
                return GenericResponse.error("Email does not exit", 400);
            }
        }
        return GenericResponse.error("Email does not exit", 400);
    }

    public GenericResponse<String> changePassword(String email, String token, String password) {
        ResetPassword resetPassword = resetPasswordRepository.findByEmail(email);
        if (resetPassword != null) {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                User userData = user.get();
                userData.setPassword(passwordEncoder.encode(password));
                logger.info("Token {}   ", token);
                try {
                    userRepository.save(userData);
                    return GenericResponse.success("Password Changed Successfully", null);
                } catch (Exception e) {
                    logger.error(e.getCause().getMessage());
                    return GenericResponse.error("Some thing went wrong", 500);
                }
            } else {
                return GenericResponse.error("Some thing went wrong", 500);
            }
        }
        return GenericResponse.error("Some thing went wrong", 400);
    }

    private void sendEmail(EmailData email, boolean isFromReset) {
        String hyperLink;
        String messageBody;
        String name = Optional.of(email.getName()).orElse("");
        if (isFromReset) {
            hyperLink = frontEndAppURL + "/auth/reset-password?token=" + email.getToken() + "&email=" + email.getEmail() + "&passReset=" + true;
            messageBody = "Hi " + name + "<br>" + "for reset password and continue your account and set a password. <br><br>" +
                    "<a href='" + hyperLink + "'>Reset password</a> <br><br>" +
                    "Once you reset an account password, you will be able to check out faster with your saved information, view your profile, bookings, and orders. <br><br>" +
                    "Thanks, <br>" + "Team ANPR";
        } else {
            hyperLink = frontEndAppURL + "/auth/activate-user?token=" + email.getToken() + "&email=" + email.getEmail();
            messageBody = "Hi " + name + "<br>" + "Thanks for creating an account with us. Please click the link below to activate your account and set a password. <br><br>" +
                    "<a href='" + hyperLink + "'>Activate account</a> <br><br>" +
                    "Once you create an account, you will be able to check out faster with your saved information, view your profile, bookings, and orders. <br><br>" +
                    "If you did not create an account with us, please disregard this email.<br><br>" +
                    "Thanks, <br>" + "Team ANPR";
        }
        email.setMessageBody(messageBody);
        email.setHyperLink(hyperLink);
        emailService.sendEmail(email);
    }

}
