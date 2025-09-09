package br.adv.cra.controller;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.entity.Usuario;
import br.adv.cra.service.SolicitacaoService;
import br.adv.cra.service.StatusSolicitacaoService;
import br.adv.cra.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import br.adv.cra.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing requests.
 * 
 * This controller provides full CRUD operations for requests and specialized
 * endpoints for request management. Requests represent tasks or actions that
 * need to be processed in the system.
 * 
 * Base URL: /api/solicitacoes
 */
@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {
    
    private final SolicitacaoService solicitacaoService;
    private final StatusSolicitacaoService statusSolicitacaoService;
    private final UsuarioService usuarioService; // Added to fetch usuario by ID
    
    /**
     * Creates a new request.
     * 
     * @param solicitacao The request information to create
     * @return The created request with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<Solicitacao> criar(@Valid @RequestBody Solicitacao solicitacao) {
        try {
            Solicitacao novaSolicitacao = solicitacaoService.salvar(solicitacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing request.
     * 
     * @param id The ID of the request to update
     * @param solicitacao The updated request information
     * @return The updated request, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Solicitacao> atualizar(@PathVariable Long id, @Valid @RequestBody Solicitacao solicitacao) {
        try {
            // Check if the solicitacao exists
            if (!solicitacaoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Set the ID to ensure we're updating the correct entity
            solicitacao.setId(id);
            
            // Ensure the datasolictacao is not null to prevent issues
            Solicitacao solicitacaoAtualizada = solicitacaoService.atualizar(solicitacao);
            return ResponseEntity.ok(solicitacaoAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<Solicitacao> setStatus(@PathVariable Long id, @PathVariable Long statusId) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Check if the current user has permission to change the status
            Solicitacao solicitacao = solicitacaoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            
            // If the solicitacao is already concluded, only ADVOGADO (lawyer) and ADMIN can change the status
            if (solicitacao.getStatusSolicitacao() != null && 
                "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
                
                boolean hasPermission = false;
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_ADVOGADO".equals(role)) {
                        hasPermission = true;
                        break;
                    }
                }
                
                if (!hasPermission) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            Solicitacao updatedSolicitacao = solicitacaoService.setStatus(id, statusId);
            return ResponseEntity.ok(updatedSolicitacao);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/status-nome/{statusNome}")
    public ResponseEntity<Solicitacao> setStatusPorNome(@PathVariable Long id, @PathVariable String statusNome) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Check if the current user has permission to change the status
            Solicitacao solicitacao = solicitacaoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            
            // If the solicitacao is already concluded, only ADVOGADO (lawyer) and ADMIN can change the status
            if (solicitacao.getStatusSolicitacao() != null && 
                "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
                
                boolean hasPermission = false;
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_ADVOGADO".equals(role)) {
                        hasPermission = true;
                        break;
                    }
                }
                
                if (!hasPermission) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            Solicitacao updatedSolicitacao = solicitacaoService.setStatusPorNome(id, statusNome);
            return ResponseEntity.ok(updatedSolicitacao);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all requests.
     * 
     * @return List of all requests
     */
    @GetMapping
    public ResponseEntity<List<Solicitacao>> listarTodas() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarTodas();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a request by ID.
     * 
     * @param id The ID of the request to retrieve
     * @return The request if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Solicitacao> buscarPorId(@PathVariable Long id) {
        try {
            return solicitacaoService.buscarPorId(id)
                    .map(solicitacao -> ResponseEntity.ok(solicitacao))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists pending requests.
     * 
     * @return List of pending requests
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<Solicitacao>> listarPendentes() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarPendentes();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists completed requests.
     * 
     * @return List of completed requests
     */
    @GetMapping("/concluidas")
    public ResponseEntity<List<Solicitacao>> listarConcluidas() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarConcluidas();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists overdue requests.
     * 
     * @return List of overdue requests
     */
    @GetMapping("/atrasadas")
    public ResponseEntity<List<Solicitacao>> listarAtrasadas() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarAtrasadas();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists paid requests.
     * 
     * @return List of paid requests
     */
    @GetMapping("/pagas")
    public ResponseEntity<List<Solicitacao>> listarPagas() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarPagas();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists unpaid requests.
     * 
     * @return List of unpaid requests
     */
    @GetMapping("/nao-pagas")
    public ResponseEntity<List<Solicitacao>> listarNaoPagas() {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.listarNaoPagas();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by user ID, where the user is associated with the correspondente of the requests.
     * 
     * @param usuarioId The user ID to search for
     * @return List of requests for the specified user's correspondente
     */
    @GetMapping("/usuario/{usuarioId}/correspondente")
    public ResponseEntity<List<Solicitacao>> buscarPorUsuarioCorrespondente(@PathVariable Long usuarioId) {
        try {
            // First, fetch the usuario by ID
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Check if the usuario has a correspondente
            if (usuario.getCorrespondente() == null) {
                return ResponseEntity.ok(List.of()); // Return empty list if no correspondente
            }
            
            // Fetch solicitacoes by the usuario's correspondente
            Correspondente correspondente = usuario.getCorrespondente();
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorCorrespondente(correspondente);
            
            return ResponseEntity.ok(solicitacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by user.
     * 
     * @param usuarioId The user ID to search for
     * @return List of requests for the specified user
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Solicitacao>> buscarPorUsuario(@PathVariable Long usuarioId) {
        try {
            // Create a Usuario object with the ID to pass to the service
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorUsuario(usuario);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by correspondente.
     * 
     * @param correspondenteId The correspondente ID to search for
     * @return List of requests for the specified correspondente
     */
    @GetMapping("/correspondente/{correspondenteId}")
    public ResponseEntity<List<Solicitacao>> buscarPorCorrespondente(@PathVariable Long correspondenteId) {
        try {
            // Create a Correspondente object with the ID to pass to the service
            Correspondente correspondente = new Correspondente();
            correspondente.setId(correspondenteId);
            
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorCorrespondente(correspondente);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches requests by date range.
     * 
     * @param inicio The start date/time
     * @param fim The end date/time
     * @return List of requests within the specified date range
     */
    @GetMapping("/buscar/periodo")
    public ResponseEntity<List<Solicitacao>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorPeriodo(inicio, fim);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches requests by text (partial match).
     * 
     * @param texto The text to search for
     * @return List of matching requests
     */
    @GetMapping("/buscar/texto")
    public ResponseEntity<List<Solicitacao>> buscarPorTexto(@RequestParam String texto) {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorTexto(texto);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by group.
     * 
     * @param grupo The group ID to search for
     * @return List of requests in the specified group
     */
    @GetMapping("/buscar/grupo/{grupo}")
    public ResponseEntity<List<Solicitacao>> buscarPorGrupo(@PathVariable Integer grupo) {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorGrupo(grupo);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by external status.
     * 
     * @param status The external status to search for
     * @return List of requests with the specified external status
     */
    @GetMapping("/buscar/status/{status}")
    public ResponseEntity<List<Solicitacao>> buscarPorStatus(@PathVariable String status) {
        try {
            List<Solicitacao> solicitacoes = solicitacaoService.buscarPorStatusExterno(status);
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as completed.
     * 
     * @param id The ID of the request to complete
     * @param observacao Optional observation note
     * @return The completed request, or error response
     */
    @PutMapping("/{id}/concluir")
    public ResponseEntity<Solicitacao> concluir(@PathVariable Long id, @RequestBody(required = false) String observacao) {
        try {
            Solicitacao solicitacao = solicitacaoService.concluir(id, observacao);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as paid.
     * 
     * @param id The ID of the request to mark as paid
     * @return The updated request, or error response
     */
    @PutMapping("/{id}/marcar-pago")
    public ResponseEntity<Solicitacao> marcarComoPago(@PathVariable Long id) {
        try {
            Solicitacao solicitacao = solicitacaoService.marcarComoPago(id);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as unpaid.
     * 
     * @param id The ID of the request to mark as unpaid
     * @return The updated request, or error response
     */
    @PutMapping("/{id}/marcar-nao-pago")
    public ResponseEntity<Solicitacao> marcarComoNaoPago(@PathVariable Long id) {
        try {
            Solicitacao solicitacao = solicitacaoService.marcarComoNaoPago(id);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Counts pending requests.
     * 
     * @return The count of pending requests
     */
    @GetMapping("/estatisticas/pendentes")
    public ResponseEntity<Long> contarPendentes() {
        try {
            Long count = solicitacaoService.contarPendentes();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a request.
     * 
     * @param id The ID of the request to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            solicitacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}