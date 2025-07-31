package com.cosmicdoc.opdmanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CorsFilter extends OncePerRequestFilter {

    private static final List<String> DEFAULT_ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost:4200",
        "http://localhost:3001",
        "https://healthcare-app-1078740886343.us-central1.run.app",
        "https://healthcare-app-145837205370.asia-south1.run.app",
        "https://healthcare-app-191932434541.asia-south1.run.app"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Get allowed origins from environment variable or use defaults
        String allowedOriginsEnv = System.getenv("ALLOWED_ORIGINS");
        List<String> allowedOrigins = allowedOriginsEnv != null && !allowedOriginsEnv.trim().isEmpty()
                ? Arrays.asList(allowedOriginsEnv.split(","))
                : DEFAULT_ALLOWED_ORIGINS;

        // Get the request origin
        String origin = request.getHeader("Origin");
        
        // If the request has an origin and it's in our allowed list, use it
        if (origin != null && allowedOrigins.contains(origin.trim())) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else if (origin != null) {
            // For preflight requests, we need to return a valid origin
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins.get(0));
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // For actual requests, set the CORS headers
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            
            filterChain.doFilter(request, response);
        }
    }
}
