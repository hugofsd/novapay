package com.novapay.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    // Token JWT gerado pelo JwtUtil
    private String token;

    // Tipo padrão do protocolo JWT — sempre "Bearer"
    private final String type = "Bearer";
}
