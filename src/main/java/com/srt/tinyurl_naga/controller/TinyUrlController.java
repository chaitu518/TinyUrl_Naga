package com.srt.tinyurl_naga.controller;

import com.srt.tinyurl_naga.Security.model.CustomUserDetails;
import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.service.UrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TinyUrlController {
    @Value("${publicDomain:http://localhost:8080}")
    public String publicDomain;

    private UrlService urlService;

    public TinyUrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    @PostMapping("/url")
    public ResponseEntity<ShortUrlResponse> getShortUrl(@RequestBody UrlMappingRequest urlMappingRequest) {
        UrlMapping urlMapping = urlService.shortenUrl(urlMappingRequest);
        if (urlMapping == null) {
            return ResponseEntity.notFound().build();
        }
        ShortUrlResponse shortUrlResponse = ShortUrlResponse.builder()
                .id(urlMapping.getId())
                .originalUrl(urlMapping.getOriginalUrl())
                .shortCode(urlMapping.getShortCode())
                .shortUrl(publicDomain+"/api/shortUrl/" + urlMapping.getShortCode())
                .expiresAt(urlMapping.getExpiresAt())
                .accessCount(urlMapping.getAccessCount())
                .build();
        return ResponseEntity.ok(shortUrlResponse);
    }
    @GetMapping("/shortUrl/{shortCode}")
    public ResponseEntity<Object> resolveShortCode(@PathVariable String shortCode) {
        return urlService.resolveUrl(shortCode).map(m->{
            URI uri = URI.create(m.getOriginalUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return ResponseEntity.status(302).headers(headers).build();
        }).orElseGet(()->ResponseEntity.notFound().build());
    }
    @GetMapping("/url")
    public Page<ShortUrlResponse> getUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return urlService.getUrls(userId, page, size, q);
    }

    @DeleteMapping("/url/{id}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable Long id) {
        try{
            urlService.deleteShortUrl(id);
            return new ResponseEntity<>("deleted", HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("record with id : "+id+" not deleted", HttpStatus.BAD_REQUEST);
        }
    }

}
