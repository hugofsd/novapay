package com.novapay.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    // BCrypt com custo 10 — seguro e lento o suficiente para dificultar brute force
    // Usado no AuthService para encode() no registro e matches() no login
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

        }
    }