package com.srt.tinyurl_naga.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class EmailServiceImpl implements EmailService {
    private final UserRepository userRepository;
    private final Resend resend;
    private JwtService jwtService;
    public EmailServiceImpl(UserRepository userRepository, @Value("${resend.api.key}") String apiKey, JwtService jwtService) {
        this.userRepository = userRepository;
        this.resend = new Resend(apiKey);
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
    public void sendVerificationEmail(String to, String verificationLink) throws ResendException {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from("TinyURL <noreply@chaitu.theraja.in>")
                .to(to)
                .subject("Verify your email")
                .html("""
                        <h2>Email Verification</h2>
                        <p>Please verify your email:</p>
                        <a href="%s">Verify Email</a>
                        <br/><br/>
                        <small>This link expires in 24 hours</small>
                        """.formatted(verificationLink))
                .build();

        resend.emails().send(email);
    }
}
