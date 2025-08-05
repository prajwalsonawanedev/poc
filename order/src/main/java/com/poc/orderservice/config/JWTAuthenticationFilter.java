package com.poc.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.orderservice.dto.UserDetailsDto;
import com.poc.orderservice.feignclient.AuthService;
import com.poc.orderservice.utils.ObjectConverter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final AuthService authService;

    private final ObjectConverter objectConverter;

    @PostConstruct
    public void init() {
        System.out.println("JWTAuthenticationFilter Created");
        logger.info("JWTAuthenticationFilter bean created");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("JWTAuthenticationFilter: Processing request for URI: {}", request.getRequestURI());
        final String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            ResponseEntity<String> message = authService.validateToken(authHeader);

            UserDetailsDto userDetails = objectConverter.convertToObject(message.getBody(), UserDetailsDto.class);

            if (!message.getStatusCode().is2xxSuccessful()) {
                logger.warn("Token validation failed: No authentication token returned");
                filterChain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            logger.info("Token validated successfully. Setting SecurityContext for user: {}", authToken.getName());

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

