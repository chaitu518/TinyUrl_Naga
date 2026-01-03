package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.model.User;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailServiceImpl implements EmailService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private JwtService jwtService;
    public EmailServiceImpl(UserRepository userRepository, JavaMailSender mailSender, JwtService jwtService) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.jwtService = jwtService;
    }
    public String verifyEmail(String token) {
        String email = jwtService.validateEmailVerificationToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        return "Email verified successfully";
    }
    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your email - TinyUrl");
        message.setText(
                "Welcome to TinyUrl!\n\n" +
                        "Please verify your email by clicking the link below:\n\n" +
                        verificationLink + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you didn’t sign up, please ignore this email."
        );

        mailSender.send(message);
    }
}
