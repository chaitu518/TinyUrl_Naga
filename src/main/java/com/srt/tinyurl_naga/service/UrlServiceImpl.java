package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.Respository.TinyUrlRepository;
import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.model.User;
import com.srt.tinyurl_naga.utility.Base62Encoder;
import com.srt.tinyurl_naga.utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {
    private final UserRepository userRepository;
    @Value("${publicDomain:http://localhost:8080}")
    public String publicDomain;
    private final TinyUrlRepository urlRepository;
    public UrlServiceImpl(TinyUrlRepository urlRepository, UserRepository userRepository) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UrlMapping shortenUrl(UrlMappingRequest urlMappingRequest) {
        User user = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        String originalUrl = urlMappingRequest.getUrl();
        String shortCode = urlMappingRequest.getShortCode();
        Long ttl = urlMappingRequest.getTtl();
        if(originalUrl == null ) {
            throw new RuntimeException("Original url is null");
        }

        UrlMapping urlMapping = UrlMapping.builder()
                    .originalUrl(originalUrl)
                    .expiresAt(ttl!=null?Instant.now().plusSeconds(ttl):Instant.now().plusSeconds(8000))
                    .accessCount(0L)
                    .user(user)
                    .creationDate(Instant.now())
                    .build();
        UrlMapping savedUrlMapping = urlRepository.save(urlMapping);
        savedUrlMapping.setShortCode(shortCode==null?Base62Encoder.encode(savedUrlMapping.getId()):shortCode);

        return urlRepository.save(savedUrlMapping);
    }

    @Override
    public Optional<UrlMapping> resolveUrl(String shortCode) {

        Optional<UrlMapping> urlMapping = urlRepository.findByShortCode(shortCode);
        if(urlMapping.isEmpty()) {
            throw new RuntimeException("Url not found");
        }
        urlMapping.get().setAccessCount(urlMapping.get().getAccessCount()+1);
        return Optional.of(urlRepository.save(urlMapping.get()));

    }
    public List<ShortUrlResponse> getAllShortUrls() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return urlRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    private ShortUrlResponse mapToResponse(UrlMapping url) {
        return ShortUrlResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortUrl(publicDomain+"/api/shortUrl/" + url.getShortCode())
                .shortCode(url.getShortCode())
                .accessCount(url.getAccessCount())
                .expiresAt(url.getExpiresAt())
                .build();
    }

}
