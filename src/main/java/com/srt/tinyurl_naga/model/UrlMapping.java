package com.srt.tinyurl_naga.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = { @Index(name = "idx_short_code", columnList = "shortCode")})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( nullable = false)
    private String originalUrl;
    @Column(unique = true, nullable = false)
    private String shortCode;
    private Instant creationDate;
    private Instant expiresAt;
    private Long clickCount;

    @PrePersist
    public void onCreate(){
        if(this.creationDate==null)creationDate = Instant.now();
    }
}