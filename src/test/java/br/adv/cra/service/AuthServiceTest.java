package br.adv.cra.service;

import br.adv.cra.dto.JwtResponse;
import br.adv.cra.dto.LoginRequest;
import br.adv.cra.dto.RefreshTokenRequest;
import br.adv.cra.dto.RegisterRequest;
import br.adv.cra.entity.Usuario;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.repository.UsuarioRepository;
import br.adv.cra.repository.CorrespondenteRepository;
import br.adv.cra.security.JwtUtils;
import br.adv.cra.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CorrespondenteRepository correspondenteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    private Usuario testUsuario;
    private Correspondente testCorrespondente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authenticationManager, usuarioRepository, correspondenteRepository, passwordEncoder, jwtUtils);

        // Create test usuario
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setLogin("testuser");
        testUsuario.setSenha("encodedPassword");
        testUsuario.setNomecompleto("Test User");
        testUsuario.setEmailprincipal("test@example.com");
        testUsuario.setTipo(1);
        testUsuario.setAtivo(true);
        testUsuario.setDataentrada(LocalDateTime.now());

        // Create test correspondente
        testCorrespondente = new Correspondente();
        testCorrespondente.setId(1L);
        testCorrespondente.setNome("Test Correspondente");
    }

    @Test
    void testAuthenticate_ValidCredentials_ShouldReturnJwtResponse() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        UserDetailsImpl userDetails = UserDetailsImpl.build(testUsuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("jwtToken");
        when(jwtUtils.generateRefreshToken("testuser")).thenReturn("refreshToken");
        when(jwtUtils.getExpirationDateFromToken("jwtToken")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        // When
        JwtResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals("Bearer", result.getType());
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getLogin());
        assertEquals("Test User", result.getNomecompleto());
        assertEquals("test@example.com", result.getEmailprincipal());
        assertEquals(1, result.getTipo());
        assertNotNull(result.getRoles());
        assertNotNull(result.getExpiresAt());

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(userDetails);
        verify(jwtUtils, times(1)).generateRefreshToken("testuser");
    }

    @Test
    void testRegister_NewUser_ShouldReturnJwtResponse() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("newuser");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("New User");
        registerRequest.setEmailPrincipal("new@example.com");
        registerRequest.setTipo(2);

        when(usuarioRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L); // Set ID as repository would
            return usuario;
        });

        // Create a user that matches what would be saved
        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(1L);
        savedUsuario.setLogin("newuser");
        savedUsuario.setSenha("encodedPassword");
        savedUsuario.setNomecompleto("New User");
        savedUsuario.setEmailprincipal("new@example.com");
        savedUsuario.setTipo(2);
        savedUsuario.setAtivo(true);
        savedUsuario.setDataentrada(LocalDateTime.now());

        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUsuario);
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("jwtToken");
        when(jwtUtils.generateRefreshToken("newuser")).thenReturn("refreshToken");
        when(jwtUtils.getExpirationDateFromToken("jwtToken")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        // When
        JwtResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getLogin());
        assertEquals("New User", result.getNomecompleto());
        assertEquals("new@example.com", result.getEmailprincipal());
        assertEquals(2, result.getTipo());
        assertNotNull(result.getRoles());
        assertNotNull(result.getExpiresAt());

        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("newuser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(jwtUtils, times(1)).generateJwtToken(any(UserDetailsImpl.class));
        verify(jwtUtils, times(1)).generateRefreshToken("newuser");
    }

    @Test
    void testRegister_ExistingLogin_ShouldThrowException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("existinguser");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("Existing User");
        registerRequest.setEmailPrincipal("existing@example.com");
        registerRequest.setTipo(2);

        when(usuarioRepository.existsByLogin("existinguser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Erro: Login já está em uso!", exception.getMessage());

        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("existinguser");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegister_CorrespondentUserWithValidCorrespondentId_ShouldAssociateCorrespondent() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("newcorrespondent");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("New Correspondent");
        registerRequest.setEmailPrincipal("correspondent@example.com");
        registerRequest.setTipo(3); // Correspondent type
        registerRequest.setCorrespondenteId(1L);

        when(usuarioRepository.existsByLogin("newcorrespondent")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(testCorrespondente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L); // Set ID as repository would
            return usuario;
        });

        // Create a user that matches what would be saved
        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(1L);
        savedUsuario.setLogin("newcorrespondent");
        savedUsuario.setSenha("encodedPassword");
        savedUsuario.setNomecompleto("New Correspondent");
        savedUsuario.setEmailprincipal("correspondent@example.com");
        savedUsuario.setTipo(3);
        savedUsuario.setAtivo(true);
        savedUsuario.setDataentrada(LocalDateTime.now());

        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUsuario);
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("jwtToken");
        when(jwtUtils.generateRefreshToken("newcorrespondent")).thenReturn("refreshToken");
        when(jwtUtils.getExpirationDateFromToken("jwtToken")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        // When
        JwtResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);

        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("newcorrespondent");
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(jwtUtils, times(1)).generateJwtToken(any(UserDetailsImpl.class));
        verify(jwtUtils, times(1)).generateRefreshToken("newcorrespondent");
    }

    @Test
    void testRegister_CorrespondentUserWithInvalidCorrespondentId_ShouldThrowException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("newcorrespondent");
        registerRequest.setSenha("password");
        registerRequest.setNomeCompleto("New Correspondent");
        registerRequest.setEmailPrincipal("correspondent@example.com");
        registerRequest.setTipo(3); // Correspondent type
        registerRequest.setCorrespondenteId(999L);

        when(usuarioRepository.existsByLogin("newcorrespondent")).thenReturn(false);
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Erro: Correspondente com ID 999 não encontrado!", exception.getMessage());

        // Verify interactions
        verify(usuarioRepository, times(1)).existsByLogin("newcorrespondent");
        verify(correspondenteRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRefreshToken_ValidRefreshToken_ShouldReturnNewJwtResponse() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");

        when(jwtUtils.validateJwtToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validRefreshToken")).thenReturn("testuser");
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(testUsuario));
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("newJwtToken");
        when(jwtUtils.generateRefreshToken("testuser")).thenReturn("newRefreshToken");
        when(jwtUtils.getExpirationDateFromToken("newJwtToken")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        UserDetailsImpl userDetails = UserDetailsImpl.build(testUsuario);

        // When
        JwtResponse result = authService.refreshToken(request);

        // Then
        assertNotNull(result);
        assertEquals("newJwtToken", result.getToken());
        assertEquals("newRefreshToken", result.getRefreshToken());
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getLogin());
        assertEquals("Test User", result.getNomecompleto());
        assertEquals("test@example.com", result.getEmailprincipal());
        assertEquals(1, result.getTipo());
        assertNotNull(result.getRoles());
        assertNotNull(result.getExpiresAt());

        // Verify interactions
        verify(jwtUtils, times(1)).validateJwtToken("validRefreshToken");
        verify(jwtUtils, times(1)).isRefreshToken("validRefreshToken");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("validRefreshToken");
        verify(usuarioRepository, times(1)).findByLogin("testuser");
        verify(jwtUtils, times(1)).generateJwtToken(any(UserDetailsImpl.class));
        verify(jwtUtils, times(1)).generateRefreshToken("testuser");
    }

    @Test
    void testRefreshToken_InvalidRefreshToken_ShouldThrowException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalidRefreshToken");

        when(jwtUtils.validateJwtToken("invalidRefreshToken")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals("Refresh token inválido!", exception.getMessage());

        // Verify interactions
        verify(jwtUtils, times(1)).validateJwtToken("invalidRefreshToken");
        verify(jwtUtils, never()).isRefreshToken(anyString());
    }

    @Test
    void testRefreshToken_NotRefreshToken_ShouldThrowException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("accessToken");

        when(jwtUtils.validateJwtToken("accessToken")).thenReturn(true);
        when(jwtUtils.isRefreshToken("accessToken")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals("Token fornecido não é um refresh token!", exception.getMessage());

        // Verify interactions
        verify(jwtUtils, times(1)).validateJwtToken("accessToken");
        verify(jwtUtils, times(1)).isRefreshToken("accessToken");
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
    }

    @Test
    void testRefreshToken_UserNotFound_ShouldThrowException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");

        when(jwtUtils.validateJwtToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validRefreshToken")).thenReturn("nonexistentuser");
        when(usuarioRepository.findByLogin("nonexistentuser")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals("Usuário não encontrado!", exception.getMessage());

        // Verify interactions
        verify(jwtUtils, times(1)).validateJwtToken("validRefreshToken");
        verify(jwtUtils, times(1)).isRefreshToken("validRefreshToken");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("validRefreshToken");
        verify(usuarioRepository, times(1)).findByLogin("nonexistentuser");
        verify(jwtUtils, never()).generateJwtToken(any(UserDetailsImpl.class));
    }

    @Test
    void testRefreshToken_InactiveUser_ShouldThrowException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");
        testUsuario.setAtivo(false); // Inactive user

        when(jwtUtils.validateJwtToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validRefreshToken")).thenReturn("testuser");
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(testUsuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals("Usuário está inativo!", exception.getMessage());

        // Verify interactions
        verify(jwtUtils, times(1)).validateJwtToken("validRefreshToken");
        verify(jwtUtils, times(1)).isRefreshToken("validRefreshToken");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("validRefreshToken");
        verify(usuarioRepository, times(1)).findByLogin("testuser");
        verify(jwtUtils, never()).generateJwtToken(any(UserDetailsImpl.class));
    }

    @Test
    void testGetCurrentUser_AuthenticatedUser_ShouldReturnUser() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.build(testUsuario);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));

        // When
        Usuario result = authService.getCurrentUser(authentication);

        // Then
        assertNotNull(result);
        assertEquals(testUsuario, result);

        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCurrentUser_UnauthenticatedUser_ShouldThrowException() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getCurrentUser(authentication);
        });

        assertEquals("Usuário não autenticado!", exception.getMessage());

        // Verify interactions
        verify(usuarioRepository, never()).findById(anyLong());
    }

    @Test
    void testGetCurrentUser_NullAuthentication_ShouldThrowException() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getCurrentUser(null);
        });

        assertEquals("Usuário não autenticado!", exception.getMessage());

        // Verify interactions
        verify(usuarioRepository, never()).findById(anyLong());
    }

    @Test
    void testGetCurrentUser_UserNotFound_ShouldThrowException() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.build(testUsuario);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getCurrentUser(authentication);
        });

        assertEquals("Usuário não encontrado!", exception.getMessage());

        // Verify interactions
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testTestPasswordHash_ValidUser_ShouldReturnHashInfo() {
        // Given
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        Map<String, Object> result = authService.testPasswordHash("testuser", "password");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.get("username"));
        assertEquals("SUCCESS", result.get("status"));
        assertEquals(true, result.get("passwordMatches"));
        assertEquals(true, result.get("userActive"));
        assertEquals(1, result.get("userType"));

        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("testuser");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
    }

    @Test
    void testTestPasswordHash_InvalidUser_ShouldReturnError() {
        // Given
        when(usuarioRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = authService.testPasswordHash("nonexistent", "password");

        // Then
        assertNotNull(result);
        assertEquals("ERROR", result.get("status"));

        // Verify interactions
        verify(usuarioRepository, times(1)).findByLogin("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testDebugJwtGeneration_ValidUsername_ShouldReturnTokenInfo() {
        // Given
        when(jwtUtils.generateTokenFromUsername("testuser")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature");
        when(jwtUtils.validateJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature")).thenReturn("testuser");

        // When
        Map<String, Object> result = authService.debugJwtGeneration("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.get("username"));
        assertEquals(true, result.get("tokenGenerated"));
        assertEquals("SUCCESS", result.get("status"));
        assertEquals(true, result.get("tokenValid"));
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature", result.get("token"));
        assertEquals(3, result.get("tokenParts"));
        assertEquals(3, result.get("expectedParts"));

        // Verify interactions
        verify(jwtUtils, times(1)).generateTokenFromUsername("testuser");
        verify(jwtUtils, times(1)).validateJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature");
    }
}