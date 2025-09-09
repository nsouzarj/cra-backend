package br.adv.cra.controller;

import br.adv.cra.entity.Uf;
import br.adv.cra.service.UfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing Brazilian states (Unidades Federativas).
 * 
 * This controller provides read-only endpoints for listing and searching Brazilian states.
 * It's primarily used by the frontend to populate dropdowns and selection fields.
 * 
 * Base URL: /api/ufs
 */
@RestController
@RequestMapping("/api/ufs")
@RequiredArgsConstructor
public class UfController {
    
    private final UfService ufService;
    
    /**
     * Endpoint to list all states (UFs) ordered by name
     * This endpoint is public and can be used by the frontend to populate dropdowns
     * 
     * @return List of all UFs ordered by name
     */
    @GetMapping
    public ResponseEntity<List<Uf>> listarTodas() {
        try {
            List<Uf> ufs = ufService.listarTodas();
            return ResponseEntity.ok(ufs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get a specific state by ID
     * 
     * @param id The ID of the UF
     * @return The UF if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Uf> buscarPorId(@PathVariable Long id) {
        try {
            return ufService.buscarPorId(id)
                    .map(uf -> ResponseEntity.ok(uf))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get a specific state by sigla (abbreviation)
     * 
     * @param sigla The sigla (abbreviation) of the UF
     * @return The UF if found, or 404 if not found
     */
    @GetMapping("/sigla/{sigla}")
    public ResponseEntity<Uf> buscarPorSigla(@PathVariable String sigla) {
        try {
            return ufService.buscarPorSigla(sigla)
                    .map(uf -> ResponseEntity.ok(uf))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to search states by name (partial match)
     * 
     * @param nome The name to search for
     * @return List of matching UFs
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<Uf>> buscarPorNome(@RequestParam String nome) {
        try {
            List<Uf> ufs = ufService.buscarPorNome(nome);
            return ResponseEntity.ok(ufs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}