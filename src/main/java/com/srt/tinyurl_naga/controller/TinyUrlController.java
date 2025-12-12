package com.srt.tinyurl_naga.controller;

import com.srt.tinyurl_naga.dto.ShortUrlResponse;
import com.srt.tinyurl_naga.dto.UrlMappingRequest;
import com.srt.tinyurl_naga.service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/url")
public class TinyUrlController {
    private UrlService urlService;
    public TinyUrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    @PostMapping
    public ResponseEntity<ShortUrlResponse> getShortUrl(@RequestBody UrlMappingRequest urlMappingRequest) {
        return null;
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
