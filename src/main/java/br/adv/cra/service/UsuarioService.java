package br.adv.cra.service;

import br.adv.cra.dto.UsuarioDTO;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Busca o usuário existente uma única vez.
        Usuario usuarioAtual = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Mantém a data de entrada original se a nova for nula.
        if (usuario.getDataentrada() == null) {
            usuario.setDataentrada(usuarioAtual.getDataentrada());
        }

        // Se a nova senha for nula ou vazia, mantém a senha antiga.
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            usuario.setSenha(usuarioAtual.getSenha());
        } else {
            // Criptografa a nova senha se ela for fornecida.
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
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
        
        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário já está inativo.");
        }
        
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    
    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (usuario.isAtivo()) {
            throw new RuntimeException("Usuário já está ativo.");
        }
        
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodosDTO() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "nomecompleto")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNomecompleto(usuario.getNomecompleto());
        dto.setLogin(usuario.getLogin());
        dto.setEmailprincipal(usuario.getEmailprincipal());
        dto.setEmailsecundario(usuario.getEmailsecundario());
        dto.setEmailresponsavel(usuario.getEmailresponsavel());
        dto.setTipo(usuario.getTipo());
        dto.setAtivo(usuario.isAtivo());
        dto.setDataentrada(usuario.getDataentrada());
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos(Sort sort) {
        return usuarioRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue(Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarAtivos(Sort sort) {
        return usuarioRepository.findByAtivoTrue(sort);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalse(Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarInativos(Sort sort) {
        return usuarioRepository.findByAtivoFalse(sort);
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
        return usuarioRepository.findByTipo(tipo, Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorTipo(Integer tipo, Sort sort) {
        return usuarioRepository.findByTipo(tipo, sort);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeCompletoContaining(nome, Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNome(String nome, Sort sort) {
        return usuarioRepository.findByNomeCompletoContaining(nome, sort);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByAnyEmail(email, Sort.by(Sort.Direction.ASC, "nomecompleto"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorEmail(String email, Sort sort) {
        return usuarioRepository.findByAnyEmail(email, sort);
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