package com.srt.tinyurl_naga.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlMappingRequest {
    private String url;
    private String shortCode;
    private Long ttl;
    private Long userId;
}
