package com.srt.tinyurl_naga.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "url_mapping",
        indexes = {
                @Index(name = "idx_short_code", columnList = "short_code"),
                @Index(name = "idx_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_code", unique = true, length = 200)
    private String shortCode;

    @Column(name = "created_at", nullable = false)
    private Instant creationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "access_count", nullable = false)
    private Long accessCount = 0L;

    @PrePersist
    public void onCreate() {
        this.creationDate = Instant.now();
        if (accessCount == null) accessCount = 0L;
    }
}
