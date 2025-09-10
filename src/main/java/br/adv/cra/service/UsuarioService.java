package br.adv.cra.service;

import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Usuario salvar(Usuario usuario) {
        if (usuario.getDataentrada() == null) {
            usuario.setDataentrada(LocalDateTime.now());
        }
        // Encrypt password before saving
        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }
    
    public Usuario atualizar(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuário não encontrado");
        }
        // Only update password if it's provided (not empty)
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
        if (usuarioExistente.isPresent()) {
            Usuario usuarioAtual = usuarioExistente.get();
            // Preserve existing password if new password is empty
            if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
                usuario.setSenha(usuarioAtual.getSenha());
            } else {
                // Encrypt new password
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
        }
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Changes the password for a user
     * 
     * @param userId The ID of the user whose password should be changed
     * @param newPassword The new password (will be encrypted)
     * @return The updated user
     */
    public Usuario alterarSenha(Long userId, String newPassword) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Encrypt the new password
        usuario.setSenha(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }
    
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    
    public void inativar(Long id) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    
    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalse();
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(String login, String senha) {
        return usuarioRepository.findByLoginAndSenha(login, senha);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorTipo(Integer tipo) {
        return usuarioRepository.findByTipo(tipo);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeCompletoContaining(nome);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByAnyEmail(email);
    }
    
    @Transactional(readOnly = true)
    public boolean existeLogin(String login) {
        return usuarioRepository.existsByLogin(login);
    }
    
    @Transactional(readOnly = true)
    public boolean existeLoginParaOutroUsuario(String login, Long id) {
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);
        return usuario.isPresent() && !usuario.get().getId().equals(id);
    }
}