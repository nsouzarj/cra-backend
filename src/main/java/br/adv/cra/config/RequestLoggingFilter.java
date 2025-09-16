package br.adv.cra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Log request details
        logger.info("=== INCOMING REQUEST ===");
        logger.info("Method: {}", request.getMethod());
        logger.info("URL: {}://{}:{}{}", 
                   request.getScheme(), 
                   request.getServerName(), 
                   request.getServerPort(), 
                   request.getRequestURI());
        logger.info("Query String: {}", request.getQueryString());
        logger.info("Remote Address: {}", request.getRemoteAddr());
        logger.info("Content Type: {}", request.getContentType());
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("=== REQUEST COMPLETED ===");
        logger.info("Status: {}", response.getStatus());
        logger.info("Duration: {} ms", duration);
    }
}