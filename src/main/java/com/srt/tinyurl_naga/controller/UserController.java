package com.srt.tinyurl_naga.controller;

import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.Security.model.CustomUserDetails;
import com.srt.tinyurl_naga.Security.service.GoogleTokenVerifierService;
import com.srt.tinyurl_naga.dto.AuthResponse;
import com.srt.tinyurl_naga.dto.RegisterRequest;
import com.srt.tinyurl_naga.dto.UserLoginDto;
import com.srt.tinyurl_naga.model.AuthProvider;
import com.srt.tinyurl_naga.model.User;
import com.srt.tinyurl_naga.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GoogleTokenVerifierService googleVerifier;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserController(
            GoogleTokenVerifierService googleVerifier, UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.googleVerifier = googleVerifier;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if(request.getPassword().isEmpty()) throw new RuntimeException("Password cannot be empty");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setProvider(AuthProvider.LOCAL);

        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return new AuthResponse(jwtService.generateToken(user));
    }
    @PostMapping("/google")
    public AuthResponse googleLogin(@RequestBody Map<String, String> body) {

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

        return new AuthResponse(jwt);
    }


}
