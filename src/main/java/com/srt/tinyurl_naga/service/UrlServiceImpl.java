package com.srt.tinyurl_naga.service;

import com.srt.tinyurl_naga.Respository.TinyUrlRepository;
import com.srt.tinyurl_naga.Respository.UserRepository;
import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.model.User;
import com.srt.tinyurl_naga.utility.SecurityUtils;
import com.srt.tinyurl_naga.utility.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    @Transactional
    public UrlMapping shortenUrl(UrlMappingRequest request) {

        User user = userRepository
                .findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new RuntimeException("Original URL is required");
        }

        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(request.getUrl())
                .accessCount(0L)
                .user(user)
                .creationDate(Instant.now())
                .expiresAt(
                        request.getTtl() != null
                                ? Instant.now().plusSeconds(request.getTtl())
                                : Instant.now().plusSeconds(800)
                )
                .build();

        // CRITICAL FIX: flush immediately
        urlRepository.saveAndFlush(urlMapping);

        String shortCode;
        if (request.getShortCode() != null && !request.getShortCode().isBlank()) {

            if (urlRepository.existsByShortCode(request.getShortCode())) {
                throw new RuntimeException("Short code already exists");
            }
            shortCode = request.getShortCode();

        } else {
            shortCode = ShortCodeGenerator.generate(urlMapping.getId());
        }

        urlMapping.setShortCode(shortCode);

        return urlRepository.save(urlMapping);
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
    public Page<ShortUrlResponse> getUrls(
            Long userId,
            int page,
            int size,
            String q
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "creationDate")
        );

        Page<UrlMapping> result;

        if (q == null || q.isBlank()) {
            result = urlRepository.findByUserId(userId, pageable);
        } else {
            result = urlRepository.search(userId, q.toLowerCase(), pageable);
        }

        return result.map(this::mapToResponse);
    }


    @Override
    public void deleteShortUrl(Long id) {
        urlRepository.deleteById(id);
    }

    private ShortUrlResponse mapToResponse(UrlMapping url) {
        return ShortUrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortUrl(publicDomain+"/api/shortUrl/" + url.getShortCode())
                .shortCode(url.getShortCode())
                .accessCount(url.getAccessCount())
                .expiresAt(url.getExpiresAt())
                .build();
    }

}
