package com.example.inventory_service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createNewBucket());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String key = getClientIdentifier(httpRequest);
        Bucket bucket = resolveBucket(key);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"TOO_MANY_REQUESTS\", \"message\": \"Rate limit exceeded. Try again later.\"}");
        }
    }

    private String getClientIdentifier(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return "user:" + authHeader.substring(7, Math.min(authHeader.length(), 50));
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return "ip:" + xForwardedFor.split(",")[0].trim();
        }

        return "ip:" + request.getRemoteAddr();
    }
}
