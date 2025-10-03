package br.adv.cra.service;

import br.adv.cra.dto.JwtResponse;
import br.adv.cra.dto.LoginRequest;
import br.adv.cra.dto.RefreshTokenRequest;
import br.adv.cra.dto.RegisterRequest;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.CorrespondenteRepository;
import br.adv.cra.repository.UsuarioRepository;
import br.adv.cra.security.JwtUtils;
import br.adv.cra.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AuthService")
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

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private UserDetailsImpl userDetails;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("testuser");
        usuario.setSenha("hashedPassword");
        usuario.setNomecompleto("Test User");
        usuario.setEmailprincipal("test@test.com");
        usuario.setTipo(1);
        usuario.setAtivo(true);

        userDetails = UserDetailsImpl.build(usuario);

        loginRequest = new LoginRequest();
        loginRequest.setLogin("testuser");
        loginRequest.setSenha("password");

        registerRequest = new RegisterRequest();
        registerRequest.setLogin("newuser");
        registerRequest.setSenha("newpassword");
        registerRequest.setNomeCompleto("New User");
        registerRequest.setEmailPrincipal("new@test.com");
        registerRequest.setTipo(1);
    }

    @Test
    @DisplayName("Deve autenticar um usuário com sucesso e retornar um JwtResponse")
    void authenticate_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("jwt-token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtUtils.getExpirationDateFromToken(anyString())).thenReturn(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()));

        JwtResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(userDetails.getId(), response.getId());
        assertEquals(userDetails.getUsername(), response.getLogin());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com credenciais inválidas")
    void authenticate_Failure_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginRequest));
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void register_Success() {
        when(usuarioRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("jwt-token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtUtils.getExpirationDateFromToken(anyString())).thenReturn(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()));

        JwtResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve registrar um novo usuário do tipo correspondente com sucesso")
    void register_Correspondente_Success() {
        registerRequest.setTipo(3);
        registerRequest.setCorrespondenteId(10L);
        Correspondente correspondente = new Correspondente();
        correspondente.setId(10L);

        when(usuarioRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(correspondenteRepository.findById(10L)).thenReturn(Optional.of(correspondente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario savedUser = invocation.getArgument(0);
            assertEquals(correspondente, savedUser.getCorrespondente());
            return savedUser;
        });
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("jwt-token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtUtils.getExpirationDateFromToken(anyString())).thenReturn(new Date(System.currentTimeMillis() + 3600000)); // Return a valid date for any token

        authService.register(registerRequest);

        verify(correspondenteRepository, times(1)).findById(10L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com login que já existe")
    void register_Failure_LoginExists() {
        when(usuarioRepository.existsByLogin("newuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Erro: Login já está em uso!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar usuário correspondente com ID inexistente")
    void register_Failure_CorrespondenteNotFound() {
        registerRequest.setTipo(3);
        registerRequest.setCorrespondenteId(99L);

        when(usuarioRepository.existsByLogin("newuser")).thenReturn(false);
        when(correspondenteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("Erro: Correspondente com ID 99 não encontrado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve renovar o token com sucesso")
    void refreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(jwtUtils.validateJwtToken("valid-refresh-token")).thenReturn(true);
        when(jwtUtils.isRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-refresh-token")).thenReturn("testuser");
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(usuario));
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn("new-jwt-token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("new-refresh-token");
        when(jwtUtils.getExpirationDateFromToken(anyString())).thenReturn(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()));

        JwtResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-jwt-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    @DisplayName("Deve lançar exceção se o refresh token for inválido")
    void refreshToken_Failure_InvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(jwtUtils.validateJwtToken("invalid-token")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Refresh token inválido!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se o token não for um refresh token")
    void refreshToken_Failure_NotARefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("not-a-refresh-token");
        when(jwtUtils.validateJwtToken("not-a-refresh-token")).thenReturn(true);
        when(jwtUtils.isRefreshToken("not-a-refresh-token")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Token fornecido não é um refresh token!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção na renovação do token se o usuário não for encontrado")
    void refreshToken_Failure_UserNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.isRefreshToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("unknownuser");
        when(usuarioRepository.findByLogin("unknownuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Usuário não encontrado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção na renovação do token se o usuário estiver inativo")
    void refreshToken_Failure_UserInactive() {
        usuario.setAtivo(false);
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.isRefreshToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("testuser");
        when(usuarioRepository.findByLogin("testuser")).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Usuário está inativo!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar o usuário logado atualmente")
    void getCurrentUser_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario currentUser = authService.getCurrentUser(authentication);

        assertNotNull(currentUser);
        assertEquals(1L, currentUser.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário atual se não estiver autenticado")
    void getCurrentUser_Failure_NotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.getCurrentUser(authentication));
        assertEquals("Usuário não autenticado!", exception.getMessage());
    }
}