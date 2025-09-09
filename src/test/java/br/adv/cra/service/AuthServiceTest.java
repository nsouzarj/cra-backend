package br.adv.cra.service;

import br.adv.cra.dto.RegisterRequest;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.CorrespondenteRepository;
import br.adv.cra.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CorrespondenteRepository correspondenteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserWithoutCorrespondente() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testuser");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("Test User");
        registerRequest.setTipo(2); // Advogado

        when(usuarioRepository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        // When
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        // Then
        verify(usuarioRepository).existsByLogin("testuser");
        verify(passwordEncoder).encode("password");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testRegisterCorrespondenteWithValidCorrespondenteId() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testcorrespondente");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("Test Correspondente");
        registerRequest.setTipo(3); // Correspondente
        registerRequest.setCorrespondenteId(1L);

        Correspondente correspondente = new Correspondente();
        correspondente.setId(1L);
        correspondente.setNome("Test Correspondente");

        when(usuarioRepository.existsByLogin("testcorrespondente")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        // When
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        // Then
        verify(usuarioRepository).existsByLogin("testcorrespondente");
        verify(passwordEncoder).encode("password");
        verify(correspondenteRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testRegisterCorrespondenteWithInvalidCorrespondenteId() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testcorrespondente");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("Test Correspondente");
        registerRequest.setTipo(3); // Correspondente
        registerRequest.setCorrespondenteId(999L);

        when(usuarioRepository.existsByLogin("testcorrespondente")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        
        assertEquals("Erro: Correspondente com ID 999 não encontrado!", exception.getMessage());
        
        verify(usuarioRepository).existsByLogin("testcorrespondente");
        verify(passwordEncoder).encode("password");
        verify(correspondenteRepository).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}