package br.adv.cra.controller;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.service.CorrespondenteService;
import br.adv.cra.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing legal correspondents.
 * 
 * This controller provides full CRUD operations for correspondents and specialized
 * search capabilities. Correspondents are legal professionals who work with the system.
 * 
 * Base URL: /api/correspondentes
 */
@RestController
@RequestMapping("/api/correspondentes")
@RequiredArgsConstructor
public class CorrespondenteController {
    
    private final CorrespondenteService correspondenteService;
    private final EnderecoService enderecoService;
    
    /**
     * Creates a new correspondent.
     * 
     * @param correspondente The correspondent information to create
     * @return The created correspondent with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<Correspondente> criar(@Valid @RequestBody Correspondente correspondente) {
        try {
            if (correspondente.getCpfcnpj() != null && 
                correspondenteService.existeCpfCnpj(correspondente.getCpfcnpj())) {
                return ResponseEntity.badRequest().build();
            }
            
            if (correspondente.getOab() != null && 
                correspondenteService.existeOab(correspondente.getOab())) {
                return ResponseEntity.badRequest().build();
            }
            
            Correspondente novoCorrespondente = correspondenteService.salvar(correspondente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCorrespondente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing correspondent.
     * 
     * @param id The ID of the correspondent to update
     * @param correspondente The updated correspondent information
     * @return The updated correspondent, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Correspondente> atualizar(@PathVariable Long id, @Valid @RequestBody Correspondente correspondente) {
        try {
            if (!correspondenteService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (correspondente.getCpfcnpj() != null && 
                correspondenteService.existeCpfCnpjParaOutroCorrespondente(correspondente.getCpfcnpj(), id)) {
                return ResponseEntity.badRequest().build();
            }
            
            if (correspondente.getOab() != null && 
                correspondenteService.existeOabParaOutroCorrespondente(correspondente.getOab(), id)) {
                return ResponseEntity.badRequest().build();
            }
            
            correspondente.setId(id);
            Correspondente correspondenteAtualizado = correspondenteService.atualizar(correspondente);
            return ResponseEntity.ok(correspondenteAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all correspondents.
     * 
     * @return List of all correspondents
     */
    @GetMapping
    public ResponseEntity<List<Correspondente>> listarTodos() {
        try {
            List<Correspondente> correspondentes = correspondenteService.listarTodos();
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a correspondent by ID.
     * 
     * @param id The ID of the correspondent to retrieve
     * @return The correspondent if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Correspondente> buscarPorId(@PathVariable Long id) {
        try {
            return correspondenteService.buscarPorId(id)
                    .map(correspondente -> ResponseEntity.ok(correspondente))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a correspondent by ID with their solicitacoes.
     * 
     * @param id The ID of the correspondent to retrieve
     * @return The correspondent with solicitacoes if found, or 404 if not found
     */
    @GetMapping("/{id}/com-solicitacoes")
    public ResponseEntity<Correspondente> buscarPorIdComSolicitacoes(@PathVariable Long id) {
        try {
            return correspondenteService.buscarPorIdComSolicitacoes(id)
                    .map(correspondente -> ResponseEntity.ok(correspondente))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists only active correspondents.
     * 
     * @return List of active correspondents
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<Correspondente>> listarAtivos() {
        try {
            List<Correspondente> correspondentes = correspondenteService.listarAtivos();
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches correspondents by name (partial match).
     * 
     * @param nome The name to search for
     * @return List of matching correspondents
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<Correspondente>> buscarPorNome(@RequestParam String nome) {
        try {
            List<Correspondente> correspondentes = correspondenteService.buscarPorNome(nome);
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds correspondent by CPF/CNPJ.
     * 
     * @param cpfCnpj The CPF/CNPJ to search for
     * @return The correspondent if found, or 404 if not found
     */
    @GetMapping("/buscar/cpfcnpj/{cpfCnpj}")
    public ResponseEntity<Correspondente> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        try {
            return correspondenteService.buscarPorCpfCnpj(cpfCnpj)
                    .map(correspondente -> ResponseEntity.ok(correspondente))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds correspondent by OAB number.
     * 
     * @param oab The OAB number to search for
     * @return The correspondent if found, or 404 if not found
     */
    @GetMapping("/buscar/oab/{oab}")
    public ResponseEntity<Correspondente> buscarPorOab(@PathVariable String oab) {
        try {
            return correspondenteService.buscarPorOab(oab)
                    .map(correspondente -> ResponseEntity.ok(correspondente))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds correspondents by type.
     * 
     * @param tipo The correspondent type to search for
     * @return List of correspondents with the specified type
     */
    @GetMapping("/buscar/tipo/{tipo}")
    public ResponseEntity<List<Correspondente>> buscarPorTipo(@PathVariable String tipo) {
        try {
            List<Correspondente> correspondentes = correspondenteService.buscarPorTipo(tipo);
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists correspondents with rule 1.
     * 
     * @return List of correspondents with rule 1
     */
    @GetMapping("/regra1")
    public ResponseEntity<List<Correspondente>> listarComRegra1() {
        try {
            List<Correspondente> correspondentes = correspondenteService.listarComRegra1();
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists correspondents with rule 2.
     * 
     * @return List of correspondents with rule 2
     */
    @GetMapping("/regra2")
    public ResponseEntity<List<Correspondente>> listarComRegra2() {
        try {
            List<Correspondente> correspondentes = correspondenteService.listarComRegra2();
            return ResponseEntity.ok(correspondentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deactivates a correspondent.
     * 
     * @param id The ID of the correspondent to deactivate
     * @return 200 OK if successful, 404 if not found, or error response
     */
    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        try {
            correspondenteService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Activates a correspondent.
     * 
     * @param id The ID of the correspondent to activate
     * @return 200 OK if successful, 404 if not found, or error response
     */
    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        try {
            correspondenteService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a correspondent.
     * 
     * @param id The ID of the correspondent to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            correspondenteService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}