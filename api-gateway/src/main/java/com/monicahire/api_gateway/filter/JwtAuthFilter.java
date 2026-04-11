package com.monicahire.api_gateway.filter;

import com.monicahire.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
 
import java.util.List;
 
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
 
    private final JwtUtil jwtUtil;
 
    // Public endpoints — gateway lets these through without a token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/verify",
            "/api/auth/refresh",
            "/api/candidates/apply",
            "/api/candidates/submit",
            "/api/interviews/questions"  // candidates access this without auth
    );
 
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
 
        // Let public endpoints through
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
 
        // Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
 
        String token = authHeader.substring(7);
 
        // Validate token
        if (!jwtUtil.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
 
        // Extract claims and forward as headers
        String userId = jwtUtil.extractUserId(token);
        String userRole = jwtUtil.extractRole(token);
 
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", userRole)
                        .build())
                .build();
 
        return chain.filter(mutatedExchange);
    }
 
    @Override
    public int getOrder() {
        return -1; // Run before everything else
    }
 
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
 