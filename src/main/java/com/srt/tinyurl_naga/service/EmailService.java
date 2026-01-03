package com.srt.tinyurl_naga.service;

public interface EmailService {
    public String verifyEmail(String token);
    public void sendVerificationEmail(String to, String verificationLink);
}
