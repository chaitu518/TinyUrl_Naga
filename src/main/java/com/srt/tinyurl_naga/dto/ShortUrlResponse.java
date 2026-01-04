package com.srt.tinyurl_naga.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ShortUrlResponse {
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private Instant expiresAt;
    private Long accessCount;
}
