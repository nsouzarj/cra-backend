package br.adv.cra.controller;

import br.adv.cra.entity.Comarca;
import br.adv.cra.service.ComarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing court districts (Comarcas).
 * 
 * This controller provides endpoints for court district operations,
 * including CRUD operations and specialized search capabilities.
 * Court districts are administrative divisions of Brazilian states.
 * 
 * Base URL: /api/comarcas
 */
@RestController
@RequestMapping("/api/comarcas")
@RequiredArgsConstructor
public class ComarcaController {
    
    private final ComarcaService comarcaService;
    
    /**
     * Creates a new court district.
     * 
     * @param comarca The court district information to create
     * @return The created court district with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<Comarca> criar(@Valid @RequestBody Comarca comarca) {
        try {
            Comarca novaComarca = comarcaService.salvar(comarca);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaComarca);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing court district.
     * 
     * @param id The ID of the court district to update
     * @param comarca The updated court district information
     * @return The updated court district, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comarca> atualizar(@PathVariable Long id, @Valid @RequestBody Comarca comarca) {
        try {
            if (!comarcaService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            comarca.setId(id);
            Comarca comarcaAtualizada = comarcaService.atualizar(comarca);
            return ResponseEntity.ok(comarcaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all court districts ordered by name.
     * 
     * @return List of all court districts ordered by name
     */
    @GetMapping
    public ResponseEntity<List<Comarca>> listarTodas() {
        try {
            List<Comarca> comarcas = comarcaService.listarTodas();
            return ResponseEntity.ok(comarcas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a court district by ID.
     * 
     * @param id The ID of the court district to retrieve
     * @return The court district if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comarca> buscarPorId(@PathVariable Long id) {
        try {
            return comarcaService.buscarPorId(id)
                    .map(comarca -> ResponseEntity.ok(comarca))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches court districts by name (partial match).
     * 
     * @param nome The name to search for
     * @return List of matching court districts
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<Comarca>> buscarPorNome(@RequestParam String nome) {
        try {
            List<Comarca> comarcas = comarcaService.buscarPorNome(nome);
            return ResponseEntity.ok(comarcas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds court districts by state (UF) ID.
     * 
     * @param ufId The state ID to search for
     * @return List of court districts in the specified state
     */
    @GetMapping("/buscar/uf/{ufId}")
    public ResponseEntity<List<Comarca>> buscarPorUf(@PathVariable Long ufId) {
        try {
            // Note: In a real implementation, you would fetch the Uf first
            // This is simplified for demonstration
            List<Comarca> comarcas = comarcaService.listarTodas()
                    .stream()
                    .filter(c -> c.getUf() != null && c.getUf().getId().equals(ufId))
                    .toList();
            return ResponseEntity.ok(comarcas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds court districts by state abbreviation (sigla).
     * 
     * @param sigla The state abbreviation to search for
     * @return List of court districts in the specified state
     */
    @GetMapping("/buscar/uf/sigla/{sigla}")
    public ResponseEntity<List<Comarca>> buscarPorUfSigla(@PathVariable String sigla) {
        try {
            List<Comarca> comarcas = comarcaService.buscarPorUfSigla(sigla);
            return ResponseEntity.ok(comarcas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a court district.
     * 
     * @param id The ID of the court district to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            comarcaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}