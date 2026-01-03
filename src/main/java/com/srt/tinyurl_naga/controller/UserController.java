package com.srt.tinyurl_naga.controller;


import com.resend.core.exception.ResendException;
import com.srt.tinyurl_naga.dto.AuthResponse;
import com.srt.tinyurl_naga.dto.RegisterRequest;
import com.srt.tinyurl_naga.dto.UserLoginDto;

import com.srt.tinyurl_naga.service.EmailService;
import com.srt.tinyurl_naga.service.EmailServiceImpl;
import com.srt.tinyurl_naga.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) throws ResendException {
        return new ResponseEntity<>(userService.register(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginDto request) {
        return userService.login(request.getEmail(), request.getPassword());
    }
    @PostMapping("/google")
    public AuthResponse googleLogin(@RequestBody Map<String, String> body) {
        return userService.googleLogin(body);
    }
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return new ResponseEntity<>(emailService.verifyEmail(token),HttpStatus.OK);
    }


}
