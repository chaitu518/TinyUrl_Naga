package com.srt.tinyurl_naga.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ShortUrlResponse {
    private String shortUrl;
    private String shortCode;
    private Instant ttl;
}
