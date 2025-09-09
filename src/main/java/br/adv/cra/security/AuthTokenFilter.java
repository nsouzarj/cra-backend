package br.adv.cra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;
    
    public AuthTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                log.debug("JWT token found: {}...", jwt.substring(0, Math.min(20, jwt.length())));
                
                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    log.debug("JWT token valid for user: {}", username);
                    
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtils.validateJwtToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, 
                                                                       userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authentication set for user: {}", username);
                    } else {
                        log.warn("JWT token validation failed for user: {}", username);
                    }
                } else {
                    log.warn("JWT token validation failed - invalid token");
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("Full JWT parsing error: ", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}