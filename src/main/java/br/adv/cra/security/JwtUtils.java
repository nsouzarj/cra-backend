package br.adv.cra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
    
    @Value("${app.jwt.secret:craSecretKeyForJWTTokenGenerationThatNeedsToBeAtLeast256Bits}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private int jwtExpirationMs;
    
    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private int refreshTokenExpirationMs;
    
    public String generateJwtToken(UserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername());
    }
    
    public String generateTokenFromUsername(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtExpirationMs);
    }
    
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, refreshTokenExpirationMs);
    }
    
    private String createToken(Map<String, Object> claims, String subject, int expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String getUserNameFromJwtToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Boolean validateJwtToken(String authToken, UserDetails userDetails) {
        try {
            final String username = getUserNameFromJwtToken(authToken);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    public Boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    private Key getSignInKey() {
        // Use the secret directly as bytes, ensuring it's long enough for HS256
        byte[] keyBytes;
        
        // Check if the secret is Base64 encoded and decode it
        try {
            if (jwtSecret.length() > 32 && jwtSecret.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                keyBytes = Decoders.BASE64.decode(jwtSecret);
            } else {
                keyBytes = jwtSecret.getBytes();
            }
        } catch (Exception e) {
            log.debug("Using JWT secret as plain string: {}", e.getMessage());
            keyBytes = jwtSecret.getBytes();
        }
        
        // Ensure the key is at least 256 bits (32 bytes) for HS256
        if (keyBytes.length < 32) {
            log.warn("JWT secret is too short, padding to 32 bytes");
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            keyBytes = paddedKey;
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
}