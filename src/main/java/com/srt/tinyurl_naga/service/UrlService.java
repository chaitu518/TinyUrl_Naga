package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;

import java.util.List;
import java.util.Optional;

public interface UrlService {
    UrlMapping shortenUrl(UrlMappingRequest urlMappingRequest);
    Optional<UrlMapping> resolveUrl(String shortCode);
    public List<ShortUrlResponse> getAllShortUrls();
}
