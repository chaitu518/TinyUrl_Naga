package com.srt.tinyurl_naga.service;

import com.resend.core.exception.ResendException;

public interface EmailService {
    public String verifyEmail(String token);
    public void sendVerificationEmail(String to, String verificationLink) throws ResendException;
}
