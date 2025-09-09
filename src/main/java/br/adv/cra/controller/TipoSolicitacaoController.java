package br.adv.cra.controller;

import br.adv.cra.entity.TipoSolicitacao;
import br.adv.cra.service.TipoSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing request types.
 * 
 * This controller provides full CRUD operations for request types.
 * Request types define the different kinds of requests that can be made in the system.
 * 
 * Base URL: /api/tipos-solicitacao
 */
@RestController
@RequestMapping("/api/tipos-solicitacao")
@RequiredArgsConstructor
public class TipoSolicitacaoController {
    
    private final TipoSolicitacaoService tipoSolicitacaoService;
    
    /**
     * Creates a new request type.
     * 
     * @param tipoSolicitacao The request type information to create
     * @return The created request type with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<TipoSolicitacao> criar(@Valid @RequestBody TipoSolicitacao tipoSolicitacao) {
        try {
            TipoSolicitacao novoTipoSolicitacao = tipoSolicitacaoService.salvar(tipoSolicitacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoTipoSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing request type.
     * 
     * @param id The ID of the request type to update
     * @param tipoSolicitacao The updated request type information
     * @return The updated request type, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoSolicitacao> atualizar(@PathVariable Long id, @Valid @RequestBody TipoSolicitacao tipoSolicitacao) {
        try {
            if (!tipoSolicitacaoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            tipoSolicitacao.setIdtiposolicitacao(id);
            TipoSolicitacao tipoSolicitacaoAtualizado = tipoSolicitacaoService.atualizar(tipoSolicitacao);
            return ResponseEntity.ok(tipoSolicitacaoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all request types.
     * 
     * @return List of all request types
     */
    @GetMapping
    public ResponseEntity<List<TipoSolicitacao>> listarTodos() {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.listarTodos();
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all request types ordered by species.
     * 
     * @return List of all request types ordered by species
     */
    @GetMapping("/ordenados")
    public ResponseEntity<List<TipoSolicitacao>> listarTodosOrdenados() {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.listarTodosOrdenados();
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a request type by ID.
     * 
     * @param id The ID of the request type to retrieve
     * @return The request type if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoSolicitacao> buscarPorId(@PathVariable Long id) {
        try {
            return tipoSolicitacaoService.buscarPorId(id)
                    .map(tipoSolicitacao -> ResponseEntity.ok(tipoSolicitacao))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches request types by species (partial match).
     * 
     * @param especie The species to search for
     * @return List of matching request types
     */
    @GetMapping("/buscar/especie")
    public ResponseEntity<List<TipoSolicitacao>> buscarPorEspecie(@RequestParam String especie) {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.buscarPorEspecie(especie);
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches request types by description (partial match).
     * 
     * @param descricao The description to search for
     * @return List of matching request types
     */
    @GetMapping("/buscar/descricao")
    public ResponseEntity<List<TipoSolicitacao>> buscarPorDescricao(@RequestParam String descricao) {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.buscarPorDescricao(descricao);
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds request types by type.
     * 
     * @param tipo The type to search for
     * @return List of request types with the specified type
     */
    @GetMapping("/buscar/tipo/{tipo}")
    public ResponseEntity<List<TipoSolicitacao>> buscarPorTipo(@PathVariable String tipo) {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.buscarPorTipo(tipo);
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds request types by visibility.
     * 
     * @param visualizar The visibility flag to search for
     * @return List of request types with the specified visibility
     */
    @GetMapping("/buscar/visualizar/{visualizar}")
    public ResponseEntity<List<TipoSolicitacao>> buscarPorVisualizar(@PathVariable Boolean visualizar) {
        try {
            List<TipoSolicitacao> tiposSolicitacao = tipoSolicitacaoService.buscarPorVisualizar(visualizar);
            return ResponseEntity.ok(tiposSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a request type.
     * 
     * @param id The ID of the request type to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            tipoSolicitacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}