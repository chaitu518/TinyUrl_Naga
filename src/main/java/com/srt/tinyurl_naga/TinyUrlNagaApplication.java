package com.srt.tinyurl_naga;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinyUrlNagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyUrlNagaApplication.class, args);
    }
    @PostConstruct
    public void logProfile() {
        System.out.println("Active profile: " + System.getProperty("spring.profiles.active"));
    }

}
