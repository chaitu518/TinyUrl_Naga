package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.Respository.TinyUrlRepository;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {
    private TinyUrlRepository urlRepository;
    public UrlServiceImpl(TinyUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlMapping shortenUrl(UrlMappingRequest urlMappingRequest) {
        String originalUrl = urlMappingRequest.getUrl();
        String shortCode = urlMappingRequest.getShortCode();
        Long ttl = urlMappingRequest.getTtl();
        if(originalUrl == null ) {
            throw new RuntimeException("Original url is null");
        }
        if(shortCode != null ) {
            UrlMapping urlMapping = UrlMapping.builder()
                    .originalUrl(originalUrl)
                    .shortCode(shortCode)
                    .expiresAt(ttl>0?Instant.now().plusSeconds(ttl):Instant.now())
                    .clickCount(0L)
                    .creationDate(Instant.now())
                    .build();
           return urlRepository.save(urlMapping);
        }
        return null;
    }

    @Override
    public Optional<UrlMapping> resolveUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
}
