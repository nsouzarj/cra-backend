package br.adv.cra.controller;

import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.service.StatusSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing request statuses (status de solicitação).
 * 
 * This controller provides full CRUD operations for request statuses and specialized
 * search capabilities. Request statuses represent the various states a request can be in.
 * 
 * Base URL: /api/solicitacao-status
 */
@RestController
@RequestMapping("/api/solicitacao-status")
@RequiredArgsConstructor
public class StatusSolicitacaoController {
    
    private final StatusSolicitacaoService statusSolicitacaoService;
    
    /**
     * Creates a new request status.
     * 
     * @param statusSolicitacao The request status information to create
     * @return The created request status with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody StatusSolicitacao statusSolicitacao) {
        try {
            if (statusSolicitacao.getStatus() != null && 
                statusSolicitacaoService.existeStatus(statusSolicitacao.getStatus())) {
                return ResponseEntity.badRequest().body("Status já existe");
            }
            
            StatusSolicitacao novoStatus = statusSolicitacaoService.salvar(statusSolicitacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar status de solicitação: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing request status.
     * 
     * @param id The ID of the request status to update
     * @param statusSolicitacao The updated request status information
     * @return The updated request status, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody StatusSolicitacao statusSolicitacao) {
        try {
            if (!statusSolicitacaoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (statusSolicitacao.getStatus() != null && 
                statusSolicitacaoService.existeStatusParaOutroStatus(statusSolicitacao.getStatus(), id)) {
                return ResponseEntity.badRequest().body("Status já existe para outro registro");
            }
            
            statusSolicitacao.setIdstatus(id);
            StatusSolicitacao statusAtualizado = statusSolicitacaoService.atualizar(statusSolicitacao);
            return ResponseEntity.ok(statusAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar status de solicitação: " + e.getMessage());
        }
    }
    
    /**
     * Lists all request statuses.
     * 
     * @return List of all request statuses ordered by status
     */
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<StatusSolicitacao> statusList = statusSolicitacaoService.listarTodos();
            return ResponseEntity.ok(statusList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar status de solicitação: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves a request status by ID.
     * 
     * @param id The ID of the request status to retrieve
     * @return The request status if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return statusSolicitacaoService.buscarPorId(id)
                    .map(status -> ResponseEntity.ok(status))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar status de solicitação: " + e.getMessage());
        }
    }
    
    /**
     * Searches request statuses by status (partial match).
     * 
     * @param status The status to search for
     * @return List of matching request statuses
     */
    @GetMapping("/buscar/status")
    public ResponseEntity<?> buscarPorStatus(@RequestParam String status) {
        try {
            List<StatusSolicitacao> statusList = statusSolicitacaoService.buscarPorStatusContaining(status);
            return ResponseEntity.ok(statusList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar status de solicitação: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a request status.
     * 
     * @param id The ID of the request status to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            statusSolicitacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar status de solicitação: " + e.getMessage());
        }
    }
}