package br.adv.cra.controller;

import br.adv.cra.dto.LoginRequest;
import br.adv.cra.dto.RegisterRequest;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.CorrespondenteRepository;
import br.adv.cra.repository.UsuarioRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CorrespondenteRepository correspondenteRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clean up test data before each test
        // Delete solicitacoes first to avoid foreign key constraint violations
        solicitacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        // Create admin user for testing
        Usuario adminUser = new Usuario();
        adminUser.setLogin("admin");
        adminUser.setSenha(passwordEncoder.encode("admin123"));
        adminUser.setNomecompleto("Administrador do Sistema");
        adminUser.setEmailprincipal("admin@cra.com.br");
        adminUser.setTipo(1); // Admin
        adminUser.setAtivo(true);
        usuarioRepository.save(adminUser);
    }

    @Test
    void testRegisterUserWithoutCorrespondente() throws Exception {
        // First, authenticate as admin to get a valid token
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setLogin("admin");
        adminLogin.setSenha("admin123");
        
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        
        String token = "Bearer " + objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testuser");
        registerRequest.setSenha("password123");
        registerRequest.setNomeCompleto("Test User");
        registerRequest.setTipo(2); // Advogado

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk()); // Should succeed with valid admin token
    }

    @Test
    void testRegisterCorrespondenteWithCorrespondenteId() throws Exception {
        // First create a correspondente
        Correspondente correspondente = new Correspondente();
        correspondente.setNome("Test Correspondente");
        correspondente.setAtivo(true);
        Correspondente savedCorrespondente = correspondenteRepository.save(correspondente);

        // First, authenticate as admin to get a valid token
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setLogin("admin");
        adminLogin.setSenha("admin123");
        
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        
        String token = "Bearer " + objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testcorrespondente");
        registerRequest.setSenha("password123");
        registerRequest.setNomeCompleto("Test Correspondente User");
        registerRequest.setTipo(3); // Correspondente
        registerRequest.setCorrespondenteId(savedCorrespondente.getId());

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk()); // Should succeed with valid admin token
    }
}