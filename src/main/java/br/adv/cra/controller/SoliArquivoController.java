package br.adv.cra.controller;

import br.adv.cra.dto.SoliArquivoDTO;
import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.service.SoliArquivoService;
import br.adv.cra.util.SoliArquivoMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing file attachments in solicitations.
 * 
 * This controller provides endpoints for uploading, retrieving, updating, and
 * deleting
 * file attachments associated with solicitations.
 * 
 * Base URL: /api/soli-arquivos
 */
@RestController
@RequestMapping("/api/soli-arquivos")
@RequiredArgsConstructor
public class SoliArquivoController {

    private static final Logger logger = LoggerFactory.getLogger(SoliArquivoController.class);

    private final SoliArquivoService soliArquivoService;

    /**
     * Health check endpoint to verify the controller is reachable
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check endpoint called");
        return ResponseEntity.ok("SoliArquivoController is reachable");
    }

    /**
     * Upload a file attachment for a solicitacao
     * 
     * @param file          The file to upload
     * @param solicitacaoId The ID of the solicitacao to attach the file to
     * @param origem        The origin of the file (e.g., "correspondente" or user)
     * @return The created file attachment entity
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SoliArquivoDTO> uploadAnexo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("solicitacaoId") Long solicitacaoId,
            @RequestParam(value = "origem", defaultValue = "usuario") String origem) {

        try {
            logger.info("Received upload request for file: {} with size: {} bytes", file.getOriginalFilename(), file.getSize());
            logger.info("Solicitacao ID: {}, Origin: {}", solicitacaoId, origem);

            if (file.isEmpty()) {
                logger.warn("Upload request received with empty file");
                return ResponseEntity.badRequest().build();
            }

            SoliArquivo soliArquivo = soliArquivoService.salvarAnexo(file, solicitacaoId, origem);
            SoliArquivoDTO dto = SoliArquivoMapper.toDTO(soliArquivo);

            logger.info("File uploaded successfully with ID: {}", soliArquivo.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IOException e) {
            logger.error("IO Exception during file upload: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            logger.error("Runtime Exception during file upload: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download a specific file attachment by ID
     * 
     * @param id The ID of the file attachment to download
     * @return The file as a downloadable resource
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAnexo(@PathVariable Long id) {
        // Validate the ID parameter
        if (id == null || id <= 0) {
            logger.warn("Invalid ID parameter received for download: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            SoliArquivo soliArquivo = soliArquivoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

            // Download file from local storage (original behavior)
            Path filePath = Paths.get(soliArquivo.getCaminhofisico());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + soliArquivo.getNomearquivo() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Malformed URL error during file download for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            logger.error("IO Exception during file download for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error during file download for ID {}: {}", id, e.getMessage(), e);
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
    public ResponseEntity<SoliArquivoDTO> buscarPorId(@PathVariable Long id) {
        // Validate the ID parameter
        if (id == null || id <= 0) {
            logger.warn("Invalid ID parameter received: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            return soliArquivoService.buscarPorId(id)
                    .map(soliArquivo -> {
                        SoliArquivoDTO dto = SoliArquivoMapper.toDTO(soliArquivo);
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error retrieving file attachment with ID {}: {}", id, e.getMessage(), e);
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
    public ResponseEntity<List<SoliArquivoDTO>> listarAnexosPorSolicitacao(@PathVariable Long solicitacaoId) {
        try {
            List<SoliArquivo> soliArquivos = soliArquivoService.listarAnexosPorSolicitacao(solicitacaoId);
            List<SoliArquivoDTO> dtos = soliArquivos.stream()
                    .map(SoliArquivoMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a file attachment
     * 
     * @param id  The ID of the file attachment to update
     * @param dto The updated file attachment data
     * @return The updated file attachment
     */
    @PutMapping("/{id}")
    public ResponseEntity<SoliArquivoDTO> atualizar(@PathVariable Long id, @RequestBody SoliArquivoDTO dto) {
        try {
            SoliArquivo soliArquivo = SoliArquivoMapper.toEntity(dto);
            SoliArquivo soliArquivoAtualizado = soliArquivoService.atualizar(id, soliArquivo);
            SoliArquivoDTO updatedDto = SoliArquivoMapper.toDTO(soliArquivoAtualizado);
            return ResponseEntity.ok(updatedDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a file attachment
     * 
     * @param id     The ID of the file attachment to delete
     * @param origem The origin trying to delete the file (for permission check)
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
            @RequestParam(value = "origem", defaultValue = "usuario") String origem) {
        try {
            // Check if the file can be deleted by this origin
            if (!soliArquivoService.podeDeletar(id, origem)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            soliArquivoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}