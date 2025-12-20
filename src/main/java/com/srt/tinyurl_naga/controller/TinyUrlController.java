package com.srt.tinyurl_naga.controller;

import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.service.UrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/url")
public class TinyUrlController {
    private UrlService urlService;
    @Value("${publicDomain}")
    private String publicDomain;
    public TinyUrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    @PostMapping
    public ResponseEntity<ShortUrlResponse> getShortUrl(@RequestBody UrlMappingRequest urlMappingRequest) {
        UrlMapping urlMapping = urlService.shortenUrl(urlMappingRequest);
        if (urlMapping == null) {
            return ResponseEntity.notFound().build();
        }
        ShortUrlResponse shortUrlResponse = new ShortUrlResponse();
        shortUrlResponse.setShortCode(urlMapping.getShortCode());
        shortUrlResponse.setShortUrl(publicDomain+"/api/url/" + urlMapping.getShortCode());
        return ResponseEntity.ok(shortUrlResponse);
    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Object> resolveShortCode(@PathVariable String shortCode) {
        return urlService.resolveUrl(shortCode).map(m->{
            URI uri = URI.create(m.getOriginalUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return ResponseEntity.status(302).headers(headers).build();
        }).orElseGet(()->ResponseEntity.notFound().build());
    }

}
