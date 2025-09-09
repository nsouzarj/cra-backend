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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final CorrespondenteRepository correspondenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getSenha())
        );
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = new Usuario();
        usuario.setId(userDetails.getId());
        usuario.setLogin(userDetails.getUsername());
        usuario.setSenha(userDetails.getPassword());
        usuario.setNomecompleto(userDetails.getNomeCompleto());
        usuario.setEmailprincipal(userDetails.getEmail());
        usuario.setTipo(userDetails.getTipo());
        usuario.setAtivo(userDetails.isEnabled());
        
        String jwt = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        
        LocalDateTime expiresAt = jwtUtils.getExpirationDateFromToken(jwt)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        log.info("User {} authenticated successfully", userDetails.getUsername());
        
        return new JwtResponse(
                jwt,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getNomeCompleto(),
                userDetails.getEmail(),
                userDetails.getTipo(),
                roles,
                expiresAt
        );
    }
    
    public JwtResponse register(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByLogin(registerRequest.getLogin())) {
            throw new RuntimeException("Erro: Login já está em uso!");
        }
        
        Usuario usuario = new Usuario();
        usuario.setLogin(registerRequest.getLogin());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getSenha()));
        usuario.setNomecompleto(registerRequest.getNomeCompleto());
        usuario.setEmailprincipal(registerRequest.getEmailPrincipal());
        usuario.setEmailsecundario(registerRequest.getEmailSecundario());
        usuario.setEmailresponsavel(registerRequest.getEmailResponsavel());
        usuario.setTipo(registerRequest.getTipo());
        usuario.setAtivo(true);
        usuario.setDataentrada(LocalDateTime.now());
        
        // If user type is "correspondente" (type 3) and a correspondent ID is provided, associate it
        if (registerRequest.getTipo() == 3 && registerRequest.getCorrespondenteId() != null) {
            Optional<Correspondente> correspondenteOpt = correspondenteRepository.findById(registerRequest.getCorrespondenteId());
            if (correspondenteOpt.isPresent()) {
                usuario.setCorrespondente(correspondenteOpt.get());
            } else {
                throw new RuntimeException("Erro: Correspondente com ID " + registerRequest.getCorrespondenteId() + " não encontrado!");
            }
        }
        
        usuario = usuarioRepository.save(usuario);
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(usuario);
        String jwt = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        
        LocalDateTime expiresAt = jwtUtils.getExpirationDateFromToken(jwt)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        log.info("User {} registered successfully", userDetails.getUsername());
        
        return new JwtResponse(
                jwt,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getNomeCompleto(),
                userDetails.getEmail(),
                userDetails.getTipo(),
                roles,
                expiresAt
        );
    }
    
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Refresh token inválido!");
        }
        
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token fornecido não é um refresh token!");
        }
        
        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        
        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário está inativo!");
        }
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(usuario);
        String newJwt = jwtUtils.generateJwtToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        
        LocalDateTime expiresAt = jwtUtils.getExpirationDateFromToken(newJwt)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        log.info("Token refreshed for user {}", userDetails.getUsername());
        
        return new JwtResponse(
                newJwt,
                newRefreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getNomeCompleto(),
                userDetails.getEmail(),
                userDetails.getTipo(),
                roles,
                expiresAt
        );
    }
    
    @Transactional(readOnly = true)
    public Usuario getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado!");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // Reload from database to get fresh data
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> testPasswordHash(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Usuario usuario = usuarioRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
            
            String storedHash = usuario.getSenha();
            boolean matches = passwordEncoder.matches(password, storedHash);
            
            result.put("username", username);
            result.put("passwordProvided", password);
            result.put("storedHashPrefix", storedHash.substring(0, Math.min(20, storedHash.length())) + "...");
            result.put("passwordMatches", matches);
            result.put("userActive", usuario.isAtivo());
            result.put("userType", usuario.getTipo());
            result.put("hashLength", storedHash.length());
            result.put("hashValidFormat", storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$"));
            
            if (matches) {
                result.put("status", "SUCCESS");
                result.put("message", "Password matches stored hash");
            } else {
                result.put("status", "FAILED");
                result.put("message", "Password does not match stored hash");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> debugJwtGeneration(String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Generate a test JWT token
            String testToken = jwtUtils.generateTokenFromUsername(username);
            
            // Analyze the token structure
            String[] tokenParts = testToken.split("\\.");
            
            result.put("username", username);
            result.put("tokenGenerated", true);
            result.put("tokenParts", tokenParts.length);
            result.put("expectedParts", 3);
            result.put("token", testToken);
            result.put("tokenLength", testToken.length());
            
            if (tokenParts.length == 3) {
                result.put("header", tokenParts[0]);
                result.put("payload", tokenParts[1]);
                result.put("signature", tokenParts[2]);
                result.put("status", "SUCCESS");
                
                // Test token validation
                boolean isValid = jwtUtils.validateJwtToken(testToken);
                result.put("tokenValid", isValid);
                
                if (isValid) {
                    try {
                        String extractedUsername = jwtUtils.getUserNameFromJwtToken(testToken);
                        result.put("extractedUsername", extractedUsername);
                        result.put("usernameMatches", username.equals(extractedUsername));
                    } catch (Exception e) {
                        result.put("extractionError", e.getMessage());
                    }
                }
            } else {
                result.put("status", "ERROR");
                result.put("message", "Token has " + tokenParts.length + " parts instead of 3");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
            result.put("exceptionClass", e.getClass().getSimpleName());
        }
        
        return result;
    }
}