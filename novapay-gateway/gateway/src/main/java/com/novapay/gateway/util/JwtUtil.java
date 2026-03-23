package com.novapay.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Injetado do application.yml — chave secreta para assinar o token
    @Value("${jwt.secret}")
    private String secret;

    // Tempo de expiração em milissegundos (86400000 = 24 horas)
    @Value("${jwt.expiration}")
    private long expiration;

    // Converte o secret (String) em chave criptográfica HMAC-SHA256
    // Deve ter pelo menos 32 caracteres (256 bits) para funcionar
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera JWT com: subject=username, iat=agora, exp=agora+expiration, assinado com HMAC-SHA256
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // Valida assinatura e expiração — qualquer problema retorna false
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrai o username do payload (campo "sub") — só chamar após isValid()
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
