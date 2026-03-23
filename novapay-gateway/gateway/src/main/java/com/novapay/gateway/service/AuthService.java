package com.novapay.gateway.service;

import com.novapay.gateway.dto.request.LoginRequest;
import com.novapay.gateway.dto.response.TokenResponse;
import com.novapay.gateway.model.User;
import com.novapay.gateway.repository.UserRepository;
import com.novapay.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenResponse login(LoginRequest request) {
        // Busca usuário — lança RuntimeException se não encontrar
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // BCrypt compara a senha enviada com o hash armazenado — nunca comparar strings direto
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        log.info("Login realizado: username={}", user.getUsername());
        return new TokenResponse(token);
    }

    public void register(LoginRequest request) {
        // Verifica duplicidade antes de salvar (evita exception de unique constraint)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Usuário já existe: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                // encode() gera hash BCrypt diferente a cada chamada (salt aleatório embutido)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        log.info("Usuário registrado: username={}", request.getUsername());
    }

}
