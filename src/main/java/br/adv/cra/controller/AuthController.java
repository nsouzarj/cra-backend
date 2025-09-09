package br.adv.cra.controller;

import br.adv.cra.dto.JwtResponse;
import br.adv.cra.dto.LoginRequest;
import br.adv.cra.dto.RefreshTokenRequest;
import br.adv.cra.dto.RegisterRequest;
import br.adv.cra.entity.Usuario;
import br.adv.cra.security.UserDetailsImpl;
import br.adv.cra.service.AuthService;
import br.adv.cra.service.DatabaseConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication and authorization operations.
 * 
 * This controller handles user authentication, registration, token management,
 * and user information retrieval. It provides endpoints for login, logout,
 * token refresh, and user validation.
 * 
 * Base URL: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final DatabaseConnectionService databaseConnectionService;
    
    /**
     * Authenticates a user and returns JWT tokens.
     * 
     * @param loginRequest The login credentials containing username and password
     * @return JwtResponse with access and refresh tokens, or error information
     */
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticate(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciais inválidas");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Registers a new user (Admin only).
     * 
     * When registering a user of type "correspondente" (type 3), you can optionally
     * associate an existing correspondent entity by providing the correspondenteId.
     * 
     * @param registerRequest The user registration information
     * @return JwtResponse with access and refresh tokens for the new user, or error information
     */
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            JwtResponse jwtResponse = authService.register(registerRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro no registro");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Refreshes an expired JWT token.
     * 
     * @param request The refresh token request containing the refresh token
     * @return JwtResponse with new access and refresh tokens, or error information
     */
    @PostMapping(value = "/refresh", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse jwtResponse = authService.refreshToken(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token inválido");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Retrieves information about the current authenticated user.
     * 
     * @param authentication The Spring Security authentication object
     * @return User information map, or error information
     */
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userDetails.getId());
            userInfo.put("login", userDetails.getUsername());
            userInfo.put("nomecompleto", userDetails.getNomeCompleto());
            userInfo.put("emailPrincipal", userDetails.getEmail());
            userInfo.put("tipo", userDetails.getTipo());
            userInfo.put("ativo", userDetails.isEnabled());
            userInfo.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuário não encontrado");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Logs out the current user.
     * 
     * In a JWT stateless setup, logout is typically handled client-side
     * by removing the token from storage. This endpoint provides a 
     * confirmation response.
     * 
     * @return Success message
     */
    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<?> logoutUser() {
        // In a JWT stateless setup, logout is typically handled client-side
        // by removing the token from storage. However, we can provide a 
        // confirmation endpoint.
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validates the current JWT token.
     * 
     * @param authentication The Spring Security authentication object
     * @return Token validation information, or error information
     */
    @GetMapping(value = "/validate", produces = "application/json")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", true);
            validation.put("user", userDetails.getUsername());
            validation.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
            
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", false);
            validation.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(validation);
        }
    }
    
    /**
     * Retrieves database connection information.
     * 
     * @return Database information map, or error information
     */
    @GetMapping(value = "/database-info", produces = "application/json")
    public ResponseEntity<?> getDatabaseInfo() {
        try {
            Map<String, Object> dbInfo = databaseConnectionService.getDatabaseInfo();
            return ResponseEntity.ok(dbInfo);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Database connection failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Tests password hashing.
     * 
     * @param request Map containing username and password to test
     * @return Password hash test results
     */
    @PostMapping(value = "/test-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            Map<String, Object> result = authService.testPasswordHash(username, password);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Test failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Debugs JWT token generation.
     * 
     * @param request Map containing username for JWT generation
     * @return JWT debug information
     */
    @PostMapping(value = "/debug-jwt", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> debugJwt(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.trim().isEmpty()) {
                username = "testuser";
            }
            
            Map<String, Object> result = authService.debugJwtGeneration(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "JWT debug failed");
            error.put("message", e.getMessage());
            error.put("stackTrace", e.getStackTrace()[0].toString());
            return ResponseEntity.badRequest().body(error);
        }
    }
}