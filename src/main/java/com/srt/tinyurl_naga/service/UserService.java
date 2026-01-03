package com.srt.tinyurl_naga.service;

import com.resend.core.exception.ResendException;
import com.srt.tinyurl_naga.dto.AuthResponse;
import com.srt.tinyurl_naga.dto.RegisterRequest;
import com.srt.tinyurl_naga.dto.UserDto;
import com.srt.tinyurl_naga.model.User;

import java.util.Map;

public interface UserService {
    public AuthResponse login(String username, String password);
    public String register(RegisterRequest registerRequest) throws ResendException;
    public AuthResponse googleLogin(Map<String, String> body);
}
