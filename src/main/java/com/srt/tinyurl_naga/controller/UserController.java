package com.srt.tinyurl_naga.controller;

import com.srt.tinyurl_naga.dto.UserDto;
import com.srt.tinyurl_naga.dto.UserLoginDto;
import com.srt.tinyurl_naga.model.User;
import com.srt.tinyurl_naga.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/login")
    public UserDto login(@RequestBody UserLoginDto userLoginDto) {
        return userService.login(userLoginDto.getEmail(),userLoginDto.getPassword());
    }
    @PostMapping("/register")
    public UserDto register(@RequestBody User user) {
        return userService.register(user);
    }
}
