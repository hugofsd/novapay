package com.novapay.gateway.controller;

import com.novapay.gateway.dto.request.LoginRequest;
import com.novapay.gateway.dto.response.TokenResponse;
import com.novapay.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /auth/login → retorna JWT se credenciais válidas
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /auth/register → cria usuário com senha em BCrypt
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody LoginRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
