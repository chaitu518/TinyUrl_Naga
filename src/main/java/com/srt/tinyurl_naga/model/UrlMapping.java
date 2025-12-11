package com.srt.tinyurl_naga.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(indexes = { @Index(name = "idx_short_code", columnList = "shortCode")})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String originalUrl;
    @Column(unique = true, nullable = false)
    private String shortCode;
    private Instant creationDate;
    private Instant expiresAt;
    private Long clickCount;
}