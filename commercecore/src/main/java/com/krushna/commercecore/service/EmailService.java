package com.krushna.commercecore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationLink = "http://localhost:8081/auth/verify-email?token=" + token;
        log.info("\n--------------------------------------------------" +
                 "\n[MOCK EMAIL SERVICE] SENDING VERIFICATION EMAIL" +
                 "\nTo: " + toEmail +
                 "\nSubject: Verify Your E-Commerce Account" +
                 "\nMessage: Please click the link below to verify your email address:" +
                 "\n" + verificationLink +
                 "\n--------------------------------------------------");
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:3000/reset-password?token=" + token; // Typical frontend port
        log.info("\n--------------------------------------------------" +
                 "\n[MOCK EMAIL SERVICE] SENDING PASSWORD RESET EMAIL" +
                 "\nTo: " + toEmail +
                 "\nSubject: Password Reset Request" +
                 "\nMessage: Use the token below or the link to reset your password:" +
                 "\nToken: " + token +
                 "\nLink: " + resetLink +
                 "\n--------------------------------------------------");
    }
}
