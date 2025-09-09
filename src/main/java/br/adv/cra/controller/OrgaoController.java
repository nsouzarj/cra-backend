package br.adv.cra.controller;

import br.adv.cra.entity.Orgao;
import br.adv.cra.service.OrgaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing court organs (órgãos).
 * 
 * This controller provides full CRUD operations for court organs and specialized
 * search capabilities. Court organs are judicial entities like tribunals and courts.
 * 
 * Base URL: /api/orgaos
 */
@RestController
@RequestMapping("/api/orgaos")
@RequiredArgsConstructor
public class OrgaoController {
    
    private final OrgaoService orgaoService;
    
    /**
     * Creates a new court organ.
     * 
     * @param orgao The court organ information to create
     * @return The created court organ with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<Orgao> criar(@Valid @RequestBody Orgao orgao) {
        try {
            if (orgao.getDescricao() != null && 
                orgaoService.existeDescricao(orgao.getDescricao())) {
                return ResponseEntity.badRequest().build();
            }
            
            Orgao novoOrgao = orgaoService.salvar(orgao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoOrgao);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing court organ.
     * 
     * @param id The ID of the court organ to update
     * @param orgao The updated court organ information
     * @return The updated court organ, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Orgao> atualizar(@PathVariable Long id, @Valid @RequestBody Orgao orgao) {
        try {
            if (!orgaoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (orgao.getDescricao() != null && 
                orgaoService.existeDescricaoParaOutroOrgao(orgao.getDescricao(), id)) {
                return ResponseEntity.badRequest().build();
            }
            
            orgao.setId(id);
            Orgao orgaoAtualizado = orgaoService.atualizar(orgao);
            return ResponseEntity.ok(orgaoAtualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all court organs.
     * 
     * @return List of all court organs ordered by description
     */
    @GetMapping
    public ResponseEntity<List<Orgao>> listarTodos() {
        try {
            System.out.println("listarTodos endpoint called");
            List<Orgao> orgaos = orgaoService.listarTodos();
            System.out.println("Returning " + orgaos.size() + " orgaos");
            return ResponseEntity.ok(orgaos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a court organ by ID.
     * 
     * @param id The ID of the court organ to retrieve
     * @return The court organ if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Orgao> buscarPorId(@PathVariable Long id) {
        try {
            return orgaoService.buscarPorId(id)
                    .map(orgao -> ResponseEntity.ok(orgao))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches court organs by description (partial match).
     * 
     * @param descricao The description to search for
     * @return List of matching court organs
     */
    @GetMapping("/buscar/descricao")
    public ResponseEntity<List<Orgao>> buscarPorDescricao(@RequestParam String descricao) {
        try {
            List<Orgao> orgaos = orgaoService.buscarPorDescricao(descricao);
            return ResponseEntity.ok(orgaos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a court organ.
     * 
     * @param id The ID of the court organ to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            orgaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}