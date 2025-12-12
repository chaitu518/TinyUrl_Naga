package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.Respository.TinyUrlRepository;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {
    private TinyUrlRepository urlRepository;
    public UrlServiceImpl(TinyUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public String shortenUrl(UrlMappingRequest urlMappingRequest) {
        return "";
    }

    @Override
    public Optional<UrlMapping> resolveUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
}
