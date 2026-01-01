package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.dto.UserDto;
import com.srt.tinyurl_naga.model.User;

public interface UserService {
    public UserDto login(String username, String password);
    public UserDto register(User user);
}
