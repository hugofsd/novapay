package com.novapay.gateway.filter;

import com.novapay.gateway.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// OncePerRequestFilter = executado uma vez por requisição (versão MVC do GlobalFilter)
// Intercepta TODAS as requisições antes de chegarem ao gateway ou ao controller
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Rotas liberadas sem autenticação — qualquer outra exige JWT válido
    private static final List<String> PUBLIC_PATHS = List.of("/auth/login", "/auth/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Rotas públicas passam sem verificação de token
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Busca o header "Authorization: Bearer <token>"
        String authHeader = request.getHeader("Authorization");

        // Header ausente ou formato errado → rejeita com 401
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Requisição sem token JWT: {}", path);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return; // Interrompe — não chama filterChain.doFilter()
        }

        // Remove o prefixo "Bearer " para obter apenas o token
        String token = authHeader.substring(7);

        // Token inválido (assinatura errada) ou expirado → rejeita com 401
        if (!jwtUtil.isValid(token)) {
            log.warn("Token inválido ou expirado: {}", path);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        log.info("Acesso autorizado — usuário: {} → {}", jwtUtil.getUsername(token), path);

        // Token válido → encaminha para o gateway rotear ao serviço de destino
        filterChain.doFilter(request, response);
    }

}
