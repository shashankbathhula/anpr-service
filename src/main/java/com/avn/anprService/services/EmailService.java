package com.avn.anprService.services;

import com.avn.anprService.dto.EmailData;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    public void sendEmail(EmailData email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            //mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
            helper.setText(email.getMessageBody(), true); // Use this or above line.
            helper.setTo(email.getEmail());
            helper.setSubject(email.getSubject());
            // helper.setFrom("abc@gmail.com");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.info("Main error is {}", e.getCause().getMessage());
        }
        // mailSender.send(email);
    }
}
