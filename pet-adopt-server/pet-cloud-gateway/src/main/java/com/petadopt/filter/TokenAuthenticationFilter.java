package com.petadopt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public TokenAuthenticationFilter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        if (HttpMethod.OPTIONS.equals(method) || isPublic(path, method)) return chain.filter(exchange);

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
            return unauthorized(exchange, "请先登录后继续操作");
        }
        String token = authorization.substring(7).trim();
        return webClientBuilder.build().get()
                .uri("http://pet-user-service/internal/auth/resolve?token={token}", token)
                .retrieve()
                .bodyToMono(AuthEnvelope.class)
                .flatMap(envelope -> {
                    if (envelope == null || envelope.getCode() != 200 || envelope.getData() == null) {
                        return unauthorized(exchange, "登录已过期，请重新登录");
                    }
                    AuthData user = envelope.getData();
                    ServerWebExchange authenticated = exchange.mutate().request(builder -> builder.headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");
                        headers.remove("X-User-Name");
                        headers.set("X-User-Id", String.valueOf(user.getId()));
                        headers.set("X-User-Role", user.getRole() == null ? "USER" : user.getRole());
                        headers.set("X-User-Name", user.getUsername() == null ? "" : user.getUsername());
                    })).build();
                    return chain.filter(authenticated);
                })
                .onErrorResume(error -> unauthorized(exchange, "认证服务暂时不可用，请稍后重试"));
    }

    private boolean isPublic(String path, HttpMethod method) {
        if ("/api/auth/login".equals(path) || "/api/auth/register".equals(path)) return true;
        if (path.startsWith("/api/categories/")) return true;
        if (HttpMethod.GET.equals(method) && path.startsWith("/api/pets")) return true;
        return HttpMethod.GET.equals(method) && path.startsWith("/api/files/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", 401);
        body.put("message", message);
        body.put("data", null);
        try {
            byte[] bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Data
    public static class AuthEnvelope {
        private int code;
        private String message;
        private AuthData data;
    }

    @Data
    public static class AuthData {
        private Long id;
        private String username;
        private String nickname;
        private String role;
    }
}
