package com.srt.tinyurl_naga.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = true)
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(nullable = false)
    private boolean emailVerified;

}
