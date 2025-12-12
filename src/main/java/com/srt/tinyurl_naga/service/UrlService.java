package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;

import java.util.Optional;

public interface UrlService {
    String shortenUrl(UrlMappingRequest urlMappingRequest);
    Optional<UrlMapping> resolveUrl(String shortCode);
}
