package br.adv.cra.service;

import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("testuser");
        usuario.setSenha("password123");
        usuario.setNomecompleto("Test User");
        usuario.setEmailprincipal("test@example.com");
        usuario.setTipo(2);
        usuario.setAtivo(true);
        usuario.setDataentrada(LocalDateTime.now());
    }
    
    @Test
    void testSalvarUsuario() {
        // Given
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin("newuser");
        novoUsuario.setSenha("newpass");
        novoUsuario.setNomecompleto("New User");
        novoUsuario.setTipo(2);
        
        // Mock password encoder
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        // When
        Usuario resultado = usuarioService.salvar(novoUsuario);
        
        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getDataentrada());
        assertTrue(resultado.isAtivo());
        verify(passwordEncoder, times(1)).encode("newpass");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
    
    @Test
    void testBuscarPorId() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        // When
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getLogin());
        verify(usuarioRepository, times(1)).findById(1L);
    }
    
    @Test
    void testBuscarPorLogin() {
        // Given
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(usuario));
        
        // When
        Optional<Usuario> resultado = usuarioService.buscarPorLogin("testuser");
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getLogin());
        verify(usuarioRepository, times(1)).findByLogin("testuser");
    }
    
    @Test
    void testAutenticar() {
        // Given
        when(usuarioRepository.findByLoginAndSenha("testuser", "password123"))
                .thenReturn(Optional.of(usuario));
        
        // When
        Optional<Usuario> resultado = usuarioService.autenticar("testuser", "password123");
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getLogin());
        verify(usuarioRepository, times(1)).findByLoginAndSenha("testuser", "password123");
    }
    
    @Test
    void testInativarUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        // When
        usuarioService.inativar(1L);
        
        // Then
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
    
    @Test
    void testInativarUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.inativar(1L);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void testExisteLogin() {
        // Given
        when(usuarioRepository.existsByLogin("testuser")).thenReturn(true);
        
        // When
        boolean resultado = usuarioService.existeLogin("testuser");
        
        // Then
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByLogin("testuser");
    }
}