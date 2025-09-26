package br.adv.cra.controller;

import br.adv.cra.dto.PasswordChangeRequest;
import br.adv.cra.entity.Usuario;
import br.adv.cra.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user accounts.
 * 
 * This controller provides full CRUD operations for users with role-based access control.
 * It handles user creation, updating, retrieval, activation/deactivation, and deletion.
 * 
 * Base URL: /api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Creates a new user (Admin only).
     * 
     * @param usuario The user information to create
     * @return The created user with HTTP 201 status, or error response
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {
        try {
            if (usuarioService.existeLogin(usuario.getLogin())) {
                return ResponseEntity.badRequest().build();
            }
            Usuario novoUsuario = usuarioService.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing user.
     * 
     * @param id The ID of the user to update
     * @param usuario The updated user information
     * @return The updated user, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            if (!usuarioService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (usuarioService.existeLoginParaOutroUsuario(usuario.getLogin(), id)) {
                return ResponseEntity.badRequest().build();
            }
            
            usuario.setId(id);
            Usuario usuarioAtualizado = usuarioService.atualizar(usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all users.
     * 
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos(
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.listarTodos(sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id The ID of the user to retrieve
     * @return The user if found, or 404 if not found
     */
    @GetMapping("/{id}")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        try {
            return usuarioService.buscarPorId(id)
                    .map(usuario -> ResponseEntity.ok(usuario))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists only active users.
     * 
     * @return List of active users
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos(
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.listarAtivos(sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds user by login.
     * 
     * @param login The login/username to search for
     * @return The user if found, or 404 if not found
     */
    @GetMapping("/buscar/login/{login}")
    public ResponseEntity<Usuario> buscarPorLogin(@PathVariable String login) {
        try {
            return usuarioService.buscarPorLogin(login)
                    .map(usuario -> ResponseEntity.ok(usuario))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches users by name (partial match).
     * 
     * @param nome The name to search for
     * @return List of matching users
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<Usuario>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.buscarPorNome(nome, sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds users by type.
     * 
     * User Types:
     * - 0: Administrator
     * - 1: Lawyer (Advogado)
     * - 2: Correspondent (Correspondente)
     * 
     * @param tipo The user type to search for
     * @return List of users with the specified type
     */
    @GetMapping("/buscar/tipo/{tipo}")
    public ResponseEntity<List<Usuario>> buscarPorTipo(
            @PathVariable Integer tipo,
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.buscarPorTipo(tipo, sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Changes user password.
     * 
     * Allows any authenticated user to change their own password by providing
     * their user ID and the new password.
     * 
     * @param request The password change request containing user ID and new password
     * @return ResponseEntity with success or error status
     */
    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@Valid @RequestBody PasswordChangeRequest request) {
        try {
            Usuario usuarioAtualizado = usuarioService.alterarSenha(request.getId(), request.getNovaSenha());
            return ResponseEntity.ok().body(usuarioAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao alterar senha: " + e.getMessage());
        }
    }
    
    /**
     * Deactivates a user (Admin only).
     * 
     * @param id The ID of the user to deactivate
     * @return ResponseEntity with success or error status
     */
    @PutMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            usuarioService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao inativar usuário: " + e.getMessage());
        }
    }
    
    /**
     * Activates a user (Admin only).
     * 
     * @param id The ID of the user to activate
     * @return ResponseEntity with success or error status
     */
    @PutMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            usuarioService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ativar usuário: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a user (Admin only).
     * 
     * @param id The ID of the user to delete
     * @return ResponseEntity with success or error status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar usuário: " + e.getMessage());
        }
    }
    
    /**
     * Lists only inactive users.
     * 
     * @return List of inactive users
     */
    @GetMapping("/inativos")
    public ResponseEntity<List<Usuario>> listarInativos(
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.listarInativos(sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches users by email.
     * 
     * @param email The email to search for
     * @return List of matching users
     */
    @GetMapping("/buscar/email")
    public ResponseEntity<List<Usuario>> buscarPorEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "nomecompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            List<Usuario> usuarios = usuarioService.buscarPorEmail(email, sort);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}