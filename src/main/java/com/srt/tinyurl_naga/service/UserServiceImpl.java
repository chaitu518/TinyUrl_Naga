package com.srt.tinyurl_naga.service;


import com.resend.core.exception.ResendException;
import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.Security.service.GoogleTokenVerifierService;
import com.srt.tinyurl_naga.dto.AuthResponse;
import com.srt.tinyurl_naga.dto.RegisterRequest;
import com.srt.tinyurl_naga.model.AuthProvider;
import com.srt.tinyurl_naga.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    @Value("${frontend.base.url}")
    public String frontendBaseUrl;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifierService googleVerifier;
    private final EmailService emailService;
    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder, GoogleTokenVerifierService googleVerifier, EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.googleVerifier = googleVerifier;
        this.emailService = emailService;
    }
    @Override
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        return new AuthResponse(user.getId(),jwtService.generateToken(user));
    }

    @Override
    public String register(RegisterRequest request) throws ResendException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if(request.getPassword().isEmpty()) throw new RuntimeException("Password cannot be empty");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        userRepository.save(user);
        String token = jwtService.generateEmailVerificationToken(user);

        String verifyLink =
                frontendBaseUrl + "/verify-email?token=" + token;
        emailService.sendVerificationEmail(request.getEmail(),verifyLink);
        return "verification Email Sent";
    }
    public AuthResponse googleLogin(Map<String, String> body) {

        String idToken = body.get("idToken");

        var payload = googleVerifier.verify(idToken);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setProvider(AuthProvider.GOOGLE);
                    u.setEmailVerified(true);
                    return userRepository.save(u);
                });

        String jwt = jwtService.generateToken(user);

        return new AuthResponse(user.getId(),jwt);
    }

}
