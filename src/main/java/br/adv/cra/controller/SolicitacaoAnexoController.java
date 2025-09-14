package br.adv.cra.controller;

import br.adv.cra.entity.SolicitacaoAnexo;
import br.adv.cra.entity.Usuario;
import br.adv.cra.service.SolicitacaoAnexoService;
import br.adv.cra.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing file attachments in solicitations.
 * 
 * This controller provides endpoints for uploading, retrieving, updating, and deleting
 * file attachments associated with solicitations.
 * 
 * Base URL: /api/solicitacoes-anexos
 */
@RestController
@RequestMapping("/api/solicitacoes-anexos")
@RequiredArgsConstructor
public class SolicitacaoAnexoController {

    private final SolicitacaoAnexoService solicitacaoAnexoService;
    private final UsuarioService usuarioService;

    /**
     * Upload a file attachment for a solicitacao
     * 
     * @param file The file to upload
     * @param solicitacaoId The ID of the solicitacao to attach the file to
     * @return The created file attachment entity
     */
    @PostMapping("/upload")
    public ResponseEntity<SolicitacaoAnexo> uploadAnexo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("solicitacaoId") Long solicitacaoId) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // Fetch the usuario by username
            Usuario usuario = usuarioService.buscarPorLogin(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            SolicitacaoAnexo anexo = solicitacaoAnexoService.salvarAnexo(file, solicitacaoId, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(anexo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all file attachments for a solicitacao
     * 
     * @param solicitacaoId The ID of the solicitacao
     * @return List of file attachments
     */
    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<List<SolicitacaoAnexo>> listarAnexosPorSolicitacao(@PathVariable Long solicitacaoId) {
        try {
            List<SolicitacaoAnexo> anexos = solicitacaoAnexoService.listarAnexosPorSolicitacao(solicitacaoId);
            return ResponseEntity.ok(anexos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific file attachment by ID
     * 
     * @param id The ID of the file attachment
     * @return The file attachment if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoAnexo> buscarPorId(@PathVariable Long id) {
        try {
            return solicitacaoAnexoService.buscarPorId(id)
                    .map(anexo -> ResponseEntity.ok(anexo))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a file attachment
     * 
     * @param id The ID of the file attachment to update
     * @param anexo The updated file attachment data
     * @return The updated file attachment
     */
    @PutMapping("/{id}")
    public ResponseEntity<SolicitacaoAnexo> atualizar(@PathVariable Long id, @RequestBody SolicitacaoAnexo anexo) {
        try {
            SolicitacaoAnexo anexoAtualizado = solicitacaoAnexoService.atualizar(id, anexo);
            return ResponseEntity.ok(anexoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a file attachment
     * 
     * @param id The ID of the file attachment to delete
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            solicitacaoAnexoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}