package br.adv.cra.controller;

import br.adv.cra.dto.JwtResponse;
import br.adv.cra.dto.LoginRequest;
import br.adv.cra.service.AuthService;
import br.adv.cra.service.DatabaseConnectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Simple unit test without any Spring context loading
class AuthControllerUnitTest {
    
    private MockMvc mockMvc;
    private AuthService authService;
    private DatabaseConnectionService databaseConnectionService;
    
    @BeforeEach
    void setUp() {
        // Create mock objects
        authService = mock(AuthService.class);
        databaseConnectionService = mock(DatabaseConnectionService.class);
        
        // Create controller and mockMvc without Spring context
        AuthController authController = new AuthController(authService, databaseConnectionService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }
    
    @Test
    void testLoginWithValidCredentials() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("testuser");
        loginRequest.setSenha("testpass");
        
        // Create response object
        JwtResponse jwtResponse = new JwtResponse(
            "test-jwt-token",
            "test-refresh-token",
            "Bearer",
            1L,
            "testuser",
            "Test User",
            "test@example.com",
            2,
            List.of("ROLE_ADVOGADO"),
            LocalDateTime.now().plusHours(24)
        );
        
        // Configure mock behavior
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(jwtResponse);
        
        // Perform the test
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"testuser\",\"senha\":\"testpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("test-refresh-token"))
                .andExpect(jsonPath("$.login").value("testuser"));
                
        // Verify the mock was called
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }
    
    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Configure mock to throw an exception
        when(authService.authenticate(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Credenciais inválidas"));
        
        // Perform the test
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"testuser\",\"senha\":\"wrongpass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas"));
                
        // Verify the mock was called
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }
    
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout realizado com sucesso"));
    }
}