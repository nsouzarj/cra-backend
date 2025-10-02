package br.adv.cra.service;

import br.adv.cra.dto.UsuarioDTO;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);

        // Create test user
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setLogin("testuser");
        testUsuario.setSenha("encodedPassword");
        testUsuario.setNomecompleto("Test User");
        testUsuario.setEmailprincipal("test@example.com");
        testUsuario.setTipo(1);
        testUsuario.setAtivo(true);
        testUsuario.setDataentrada(LocalDateTime.now());
    }

    @Test
    void testSalvar_NewUser_ShouldSaveWithCurrentDateAndEncodedPassword() {
        // Given
        Usuario newUser = new Usuario();
        newUser.setLogin("newuser");
        newUser.setSenha("plaintextPassword");
        newUser.setNomecompleto("New User");
        newUser.setEmailprincipal("new@example.com");
        newUser.setTipo(2);
        newUser.setAtivo(true);
        // dataentrada is null, should be set to current date

        Usuario savedUser = new Usuario();
        savedUser.setId(2L);
        savedUser.setLogin("newuser");
        savedUser.setSenha("encodedPassword");
        savedUser.setNomecompleto("New User");
        savedUser.setEmailprincipal("new@example.com");
        savedUser.setTipo(2);
        savedUser.setAtivo(true);
        savedUser.setDataentrada(LocalDateTime.now());

        when(passwordEncoder.encode("plaintextPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        Usuario result = usuarioService.salvar(newUser);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getLogin());
        assertEquals("encodedPassword", result.getSenha());
        assertEquals("New User", result.getNomecompleto());
        assertEquals("new@example.com", result.getEmailprincipal());
        assertEquals(2, result.getTipo());
        assertTrue(result.isAtivo());
        assertNotNull(result.getDataentrada());
        
        // Verify interactions
        verify(passwordEncoder, times(1)).encode("plaintextPassword");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testSalvar_NewUserWithNullPassword_ShouldSaveWithoutEncoding() {
        // Given
        Usuario newUser = new Usuario();
        newUser.setLogin("newuser");
        newUser.setSenha(null); // Null password
        newUser.setNomecompleto("New User");
        newUser.setEmailprincipal("new@example.com");
        newUser.setTipo(2);
        newUser.setAtivo(true);

        Usuario savedUser = new Usuario();
        savedUser.setId(2L);
        savedUser.setLogin("newuser");
        savedUser.setSenha(null);
        savedUser.setNomecompleto("New User");
        savedUser.setEmailprincipal("new@example.com");
        savedUser.setTipo(2);
        savedUser.setAtivo(true);
        savedUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        Usuario result = usuarioService.salvar(newUser);

        // Then
        assertNotNull(result);
        assertNull(result.getSenha());
        
        // Verify interactions
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testAtualizar_ExistingUser_ShouldUpdateWithEncodedPassword() {
        // Given
        Usuario updatedUser = new Usuario();
        updatedUser.setId(1L);
        updatedUser.setLogin("updateduser");
        updatedUser.setSenha("newPassword"); // New password provided
        updatedUser.setNomecompleto("Updated User");
        updatedUser.setEmailprincipal("updated@example.com");
        updatedUser.setTipo(2);
        updatedUser.setAtivo(true);

        Usuario existingUser = new Usuario();
        existingUser.setId(1L);
        existingUser.setLogin("testuser");
        existingUser.setSenha("oldEncodedPassword");
        existingUser.setNomecompleto("Test User");
        existingUser.setEmailprincipal("test@example.com");
        existingUser.setTipo(1);
        existingUser.setAtivo(true);
        existingUser.setDataentrada(LocalDateTime.now());

        Usuario savedUser = new Usuario();
        savedUser.setId(1L);
        savedUser.setLogin("updateduser");
        savedUser.setSenha("newEncodedPassword");
        savedUser.setNomecompleto("Updated User");
        savedUser.setEmailprincipal("updated@example.com");
        savedUser.setTipo(2);
        savedUser.setAtivo(true);
        savedUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        Usuario result = usuarioService.atualizar(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("updateduser", result.getLogin());
        assertEquals("newEncodedPassword", result.getSenha());
        assertEquals("Updated User", result.getNomecompleto());
        assertEquals("updated@example.com", result.getEmailprincipal());
        assertEquals(2, result.getTipo());
        assertTrue(result.isAtivo());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testAtualizar_ExistingUserWithEmptyPassword_ShouldPreserveExistingPassword() {
        // Given
        Usuario updatedUser = new Usuario();
        updatedUser.setId(1L);
        updatedUser.setLogin("updateduser");
        updatedUser.setSenha(""); // Empty password
        updatedUser.setNomecompleto("Updated User");
        updatedUser.setEmailprincipal("updated@example.com");
        updatedUser.setTipo(2);
        updatedUser.setAtivo(true);

        Usuario existingUser = new Usuario();
        existingUser.setId(1L);
        existingUser.setLogin("testuser");
        existingUser.setSenha("oldEncodedPassword");
        existingUser.setNomecompleto("Test User");
        existingUser.setEmailprincipal("test@example.com");
        existingUser.setTipo(1);
        existingUser.setAtivo(true);
        existingUser.setDataentrada(LocalDateTime.now());

        Usuario savedUser = new Usuario();
        savedUser.setId(1L);
        savedUser.setLogin("updateduser");
        savedUser.setSenha("oldEncodedPassword"); // Should preserve existing password
        savedUser.setNomecompleto("Updated User");
        savedUser.setEmailprincipal("updated@example.com");
        savedUser.setTipo(2);
        savedUser.setAtivo(true);
        savedUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        Usuario result = usuarioService.atualizar(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("oldEncodedPassword", result.getSenha()); // Should preserve existing password
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testAtualizar_NonExistentUser_ShouldThrowException() {
        // Given
        Usuario updatedUser = new Usuario();
        updatedUser.setId(999L);
        updatedUser.setLogin("nonexistent");
        updatedUser.setSenha("password");
        updatedUser.setNomecompleto("Non Existent");

        when(usuarioRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.atualizar(updatedUser);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsById(999L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAlterarSenha_ExistingUser_ShouldUpdateWithEncodedPassword() {
        // Given
        Usuario existingUser = new Usuario();
        existingUser.setId(1L);
        existingUser.setLogin("testuser");
        existingUser.setSenha("oldEncodedPassword");
        existingUser.setNomecompleto("Test User");
        existingUser.setEmailprincipal("test@example.com");
        existingUser.setTipo(1);
        existingUser.setAtivo(true);
        existingUser.setDataentrada(LocalDateTime.now());

        Usuario savedUser = new Usuario();
        savedUser.setId(1L);
        savedUser.setLogin("testuser");
        savedUser.setSenha("newEncodedPassword");
        savedUser.setNomecompleto("Test User");
        savedUser.setEmailprincipal("test@example.com");
        savedUser.setTipo(1);
        savedUser.setAtivo(true);
        savedUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        Usuario result = usuarioService.alterarSenha(1L, "newPassword");

        // Then
        assertNotNull(result);
        assertEquals("newEncodedPassword", result.getSenha());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testAlterarSenha_NonExistentUser_ShouldThrowException() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.alterarSenha(999L, "newPassword");
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testDeletar_ExistingUser_ShouldDeleteUser() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.deletar(1L);

        // Then
        // Verify interactions
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentUser_ShouldThrowException() {
        // Given
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.deletar(999L);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsById(999L);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void testInativar_ExistingUser_ShouldSetUserInactive() {
        // Given
        Usuario activeUser = new Usuario();
        activeUser.setId(1L);
        activeUser.setLogin("testuser");
        activeUser.setSenha("encodedPassword");
        activeUser.setNomecompleto("Test User");
        activeUser.setEmailprincipal("test@example.com");
        activeUser.setTipo(1);
        activeUser.setAtivo(true); // Initially active
        activeUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(activeUser);

        // When
        usuarioService.inativar(1L);

        // Then
        assertFalse(activeUser.isAtivo()); // Should be inactive now
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(activeUser);
    }

    @Test
    void testInativar_NonExistentUser_ShouldThrowException() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.inativar(999L);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAtivar_ExistingUser_ShouldSetUserActive() {
        // Given
        Usuario inactiveUser = new Usuario();
        inactiveUser.setId(1L);
        inactiveUser.setLogin("testuser");
        inactiveUser.setSenha("encodedPassword");
        inactiveUser.setNomecompleto("Test User");
        inactiveUser.setEmailprincipal("test@example.com");
        inactiveUser.setTipo(1);
        inactiveUser.setAtivo(false); // Initially inactive
        inactiveUser.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(inactiveUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(inactiveUser);

        // When
        usuarioService.ativar(1L);

        // Then
        assertTrue(inactiveUser.isAtivo()); // Should be active now
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(inactiveUser);
    }

    @Test
    void testAtivar_NonExistentUser_ShouldThrowException() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.ativar(999L);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testBuscarPorId_ExistingUser_ShouldReturnUser() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));

        // When
        Optional<Usuario> result = usuarioService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUsuario, result.get());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentUser_ShouldReturnEmpty() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Usuario> result = usuarioService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllUsersAsDTOs() {
        // Given
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setSenha("password1");
        user1.setNomecompleto("User One");
        user1.setEmailprincipal("user1@example.com");
        user1.setTipo(1);
        user1.setAtivo(true);
        user1.setDataentrada(LocalDateTime.now());

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setSenha("password2");
        user2.setNomecompleto("User Two");
        user2.setEmailprincipal("user2@example.com");
        user2.setTipo(2);
        user2.setAtivo(false);
        user2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findAll(any(Sort.class))).thenReturn(List.of(user1, user2));

        // When
        List<UsuarioDTO> result = usuarioService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User One", result.get(0).getNomecompleto());
        assertEquals("User Two", result.get(1).getNomecompleto());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarTodos_ShouldReturnAllUsers() {
        // Given
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setSenha("password1");
        user1.setNomecompleto("User One");
        user1.setEmailprincipal("user1@example.com");
        user1.setTipo(1);
        user1.setAtivo(true);
        user1.setDataentrada(LocalDateTime.now());

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setSenha("password2");
        user2.setNomecompleto("User Two");
        user2.setEmailprincipal("user2@example.com");
        user2.setTipo(2);
        user2.setAtivo(false);
        user2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findAll(any(Sort.class))).thenReturn(List.of(user1, user2));

        // When
        List<Usuario> result = usuarioService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User One", result.get(0).getNomecompleto());
        assertEquals("User Two", result.get(1).getNomecompleto());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarAtivos_ShouldReturnOnlyActiveUsers() {
        // Given
        Usuario activeUser1 = new Usuario();
        activeUser1.setId(1L);
        activeUser1.setLogin("active1");
        activeUser1.setSenha("password1");
        activeUser1.setNomecompleto("Active User One");
        activeUser1.setEmailprincipal("active1@example.com");
        activeUser1.setTipo(1);
        activeUser1.setAtivo(true);
        activeUser1.setDataentrada(LocalDateTime.now());

        Usuario activeUser2 = new Usuario();
        activeUser2.setId(2L);
        activeUser2.setLogin("active2");
        activeUser2.setSenha("password2");
        activeUser2.setNomecompleto("Active User Two");
        activeUser2.setEmailprincipal("active2@example.com");
        activeUser2.setTipo(2);
        activeUser2.setAtivo(true);
        activeUser2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByAtivoTrue(any(Sort.class))).thenReturn(List.of(activeUser1, activeUser2));

        // When
        List<Usuario> result = usuarioService.listarAtivos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isAtivo());
        assertTrue(result.get(1).isAtivo());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByAtivoTrue(any(Sort.class));
    }

    @Test
    void testListarInativos_ShouldReturnOnlyInactiveUsers() {
        // Given
        Usuario inactiveUser1 = new Usuario();
        inactiveUser1.setId(1L);
        inactiveUser1.setLogin("inactive1");
        inactiveUser1.setSenha("password1");
        inactiveUser1.setNomecompleto("Inactive User One");
        inactiveUser1.setEmailprincipal("inactive1@example.com");
        inactiveUser1.setTipo(1);
        inactiveUser1.setAtivo(false);
        inactiveUser1.setDataentrada(LocalDateTime.now());

        Usuario inactiveUser2 = new Usuario();
        inactiveUser2.setId(2L);
        inactiveUser2.setLogin("inactive2");
        inactiveUser2.setSenha("password2");
        inactiveUser2.setNomecompleto("Inactive User Two");
        inactiveUser2.setEmailprincipal("inactive2@example.com");
        inactiveUser2.setTipo(2);
        inactiveUser2.setAtivo(false);
        inactiveUser2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByAtivoFalse(any(Sort.class))).thenReturn(List.of(inactiveUser1, inactiveUser2));

        // When
        List<Usuario> result = usuarioService.listarInativos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.get(0).isAtivo());
        assertFalse(result.get(1).isAtivo());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByAtivoFalse(any(Sort.class));
    }

    @Test
    void testBuscarPorLogin_ExistingUser_ShouldReturnUser() {
        // Given
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(testUsuario));

        // When
        Optional<Usuario> result = usuarioService.buscarPorLogin("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUsuario, result.get());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("testuser");
    }

    @Test
    void testBuscarPorLogin_NonExistentUser_ShouldReturnEmpty() {
        // Given
        when(usuarioRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> result = usuarioService.buscarPorLogin("nonexistent");

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("nonexistent");
    }

    @Test
    void testBuscarPorTipo_ShouldReturnUsersWithSpecificType() {
        // Given
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setSenha("password1");
        user1.setNomecompleto("User One");
        user1.setEmailprincipal("user1@example.com");
        user1.setTipo(2);
        user1.setAtivo(true);
        user1.setDataentrada(LocalDateTime.now());

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setSenha("password2");
        user2.setNomecompleto("User Two");
        user2.setEmailprincipal("user2@example.com");
        user2.setTipo(2);
        user2.setAtivo(true);
        user2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByTipo(eq(2), any(Sort.class))).thenReturn(List.of(user1, user2));

        // When
        List<Usuario> result = usuarioService.buscarPorTipo(2);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getTipo());
        assertEquals(2, result.get(1).getTipo());
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByTipo(eq(2), any(Sort.class));
    }

    @Test
    void testBuscarPorNome_ShouldReturnUsersWithMatchingName() {
        // Given
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setSenha("password1");
        user1.setNomecompleto("John Doe");
        user1.setEmailprincipal("user1@example.com");
        user1.setTipo(1);
        user1.setAtivo(true);
        user1.setDataentrada(LocalDateTime.now());

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setSenha("password2");
        user2.setNomecompleto("Jane Doe");
        user2.setEmailprincipal("user2@example.com");
        user2.setTipo(2);
        user2.setAtivo(true);
        user2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByNomeCompletoContaining(eq("Doe"), any(Sort.class))).thenReturn(List.of(user1, user2));

        // When
        List<Usuario> result = usuarioService.buscarPorNome("Doe");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getNomecompleto().contains("Doe"));
        assertTrue(result.get(1).getNomecompleto().contains("Doe"));
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByNomeCompletoContaining(eq("Doe"), any(Sort.class));
    }

    @Test
    void testBuscarPorEmail_ShouldReturnUsersWithMatchingEmail() {
        // Given
        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setSenha("password1");
        user1.setNomecompleto("John Doe");
        user1.setEmailprincipal("john@example.com");
        user1.setTipo(1);
        user1.setAtivo(true);
        user1.setDataentrada(LocalDateTime.now());

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setSenha("password2");
        user2.setNomecompleto("Jane Smith");
        user2.setEmailprincipal("jane@example.com");
        user2.setTipo(2);
        user2.setAtivo(true);
        user2.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByAnyEmail(eq("example.com"), any(Sort.class))).thenReturn(List.of(user1, user2));

        // When
        List<Usuario> result = usuarioService.buscarPorEmail("example.com");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getEmailprincipal().contains("example.com"));
        assertTrue(result.get(1).getEmailprincipal().contains("example.com"));
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByAnyEmail(eq("example.com"), any(Sort.class));
    }

    @Test
    void testExisteLogin_ExistingLogin_ShouldReturnTrue() {
        // Given
        when(usuarioRepository.existsByLogin("testuser")).thenReturn(true);

        // When
        boolean result = usuarioService.existeLogin("testuser");

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("testuser");
    }

    @Test
    void testExisteLogin_NonExistentLogin_ShouldReturnFalse() {
        // Given
        when(usuarioRepository.existsByLogin("nonexistent")).thenReturn(false);

        // When
        boolean result = usuarioService.existeLogin("nonexistent");

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("nonexistent");
    }

    @Test
    void testExisteLoginParaOutroUsuario_SameUser_ShouldReturnFalse() {
        // Given
        Usuario user = new Usuario();
        user.setId(1L);
        user.setLogin("testuser");
        user.setSenha("password");
        user.setNomecompleto("Test User");
        user.setEmailprincipal("test@example.com");
        user.setTipo(1);
        user.setAtivo(true);
        user.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        // When
        boolean result = usuarioService.existeLoginParaOutroUsuario("testuser", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("testuser");
    }

    @Test
    void testExisteLoginParaOutroUsuario_DifferentUser_ShouldReturnTrue() {
        // Given
        Usuario user = new Usuario();
        user.setId(2L);
        user.setLogin("testuser");
        user.setSenha("password");
        user.setNomecompleto("Test User");
        user.setEmailprincipal("test@example.com");
        user.setTipo(1);
        user.setAtivo(true);
        user.setDataentrada(LocalDateTime.now());

        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        // When
        boolean result = usuarioService.existeLoginParaOutroUsuario("testuser", 1L);

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("testuser");
    }

    @Test
    void testExisteLoginParaOutroUsuario_NonExistentLogin_ShouldReturnFalse() {
        // Given
        when(usuarioRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // When
        boolean result = usuarioService.existeLoginParaOutroUsuario("nonexistent", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("nonexistent");
    }
}