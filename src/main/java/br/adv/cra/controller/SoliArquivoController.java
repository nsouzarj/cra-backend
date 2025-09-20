package br.adv.cra.controller;

import br.adv.cra.dto.SoliArquivoDTO;
import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.service.SoliArquivoService;
import br.adv.cra.util.SoliArquivoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
@Tag(name = "soli-arquivo", description = "Operações relacionadas a arquivos anexados às solicitações")
public class SoliArquivoController {

    private static final Logger logger = LoggerFactory.getLogger(SoliArquivoController.class);

    private final SoliArquivoService soliArquivoService;
    
    @Value("${google.drive.oauth.enabled:false}")
    private boolean googleDriveEnabled;

    /**
     * Health check endpoint to verify the controller is reachable
     */
    @GetMapping("/health")
    @Operation(
        summary = "Verificação de saúde do controlador",
        description = "Endpoint para verificar se o controlador de arquivos está acessível"
    )
    @ApiResponse(responseCode = "200", description = "Controlador está acessível")
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
     * @param storageLocation Where to store the file ("local" or "google_drive")
     * @return The created file attachment entity
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Fazer upload de um arquivo",
        description = "Faz upload de um arquivo anexado a uma solicitação, com opção de armazenamento local ou no Google Drive"
    )
    @ApiResponse(responseCode = "201", description = "Arquivo carregado com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SoliArquivoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<?> uploadAnexo(
            @Parameter(description = "Arquivo a ser carregado", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID da solicitação à qual o arquivo será anexado", required = true)
            @RequestParam("solicitacaoId") Long solicitacaoId,
            @Parameter(description = "Origem do arquivo (ex: correspondente ou usuario)", example = "usuario")
            @RequestParam(value = "origem", defaultValue = "usuario") String origem,
            @Parameter(description = "Local de armazenamento (local ou google_drive)", example = "local")
            @RequestParam(value = "storageLocation", defaultValue = "local") String storageLocation) {

        try {
            logger.info("Received upload request for file: {} with size: {} bytes", file.getOriginalFilename(), file.getSize());
            logger.info("Solicitacao ID: {}, Origin: {}, Storage Location: {}", 
                       solicitacaoId, origem, storageLocation);

            if (file.isEmpty()) {
                logger.warn("Upload request received with empty file");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "File is empty");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate storage location
            if ("google_drive".equals(storageLocation) && !googleDriveEnabled) {
                logger.warn("Google Drive storage requested but not enabled");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Google Drive storage is not enabled");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            SoliArquivo soliArquivo = soliArquivoService.salvarAnexo(file, solicitacaoId, origem, storageLocation);
            SoliArquivoDTO dto = SoliArquivoMapper.toDTO(soliArquivo);

            logger.info("File uploaded successfully with ID: {}", soliArquivo.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IOException e) {
            logger.error("IO Exception during file upload: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("Runtime Exception during file upload: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred during file upload");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Download a specific file attachment by ID
     * 
     * @param id The ID of the file attachment to download
     * @return The file as a downloadable resource
     */
    @GetMapping("/{id}/download")
    @Operation(
        summary = "Baixar um arquivo",
        description = "Baixa um arquivo anexado, recuperando-o do armazenamento local ou do Google Drive"
    )
    @ApiResponse(responseCode = "200", description = "Arquivo baixado com sucesso")
    @ApiResponse(responseCode = "400", description = "ID inválido")
    @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Resource> downloadAnexo(
        @Parameter(description = "ID do arquivo a ser baixado", required = true)
        @PathVariable Long id
    ) {
        // Validate the ID parameter
        if (id == null || id <= 0) {
            logger.warn("Invalid ID parameter received for download: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            SoliArquivo soliArquivo = soliArquivoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

            // Get file content as InputStream
            InputStream fileStream = soliArquivoService.getFileContent(id);
            InputStreamResource resource = new InputStreamResource(fileStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + soliArquivo.getNomearquivo() + "\"")
                    .body(resource);
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
    @Operation(
        summary = "Obter informações de um arquivo",
        description = "Obtém as informações de um arquivo anexado específico por ID"
    )
    @ApiResponse(responseCode = "200", description = "Informações do arquivo retornadas com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SoliArquivoDTO.class)))
    @ApiResponse(responseCode = "400", description = "ID inválido")
    @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<SoliArquivoDTO> buscarPorId(
        @Parameter(description = "ID do arquivo a ser obtido", required = true)
        @PathVariable Long id
    ) {
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
    @Operation(
        summary = "Listar arquivos de uma solicitação",
        description = "Obtém todos os arquivos anexados a uma solicitação específica"
    )
    @ApiResponse(responseCode = "200", description = "Lista de arquivos retornada com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SoliArquivoDTO.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<List<SoliArquivoDTO>> listarAnexosPorSolicitacao(
        @Parameter(description = "ID da solicitação para listar os arquivos", required = true)
        @PathVariable Long solicitacaoId
    ) {
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
    @Operation(
        summary = "Atualizar informações de um arquivo",
        description = "Atualiza as informações de um arquivo anexado existente"
    )
    @ApiResponse(responseCode = "200", description = "Arquivo atualizado com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SoliArquivoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<SoliArquivoDTO> atualizar(
        @Parameter(description = "ID do arquivo a ser atualizado", required = true)
        @PathVariable Long id,
        @Parameter(description = "Dados atualizados do arquivo", required = true)
        @RequestBody SoliArquivoDTO dto
    ) {
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
    @Operation(
        summary = "Excluir um arquivo",
        description = "Exclui um arquivo anexado, removendo-o do armazenamento local ou do Google Drive"
    )
    @ApiResponse(responseCode = "204", description = "Arquivo excluído com sucesso")
    @ApiResponse(responseCode = "403", description = "Permissão negada")
    @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Void> deletar(
        @Parameter(description = "ID do arquivo a ser excluído", required = true)
        @PathVariable Long id,
        @Parameter(description = "Origem da exclusão (para verificação de permissão)", example = "usuario")
        @RequestParam(value = "origem", defaultValue = "usuario") String origem
    ) {
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