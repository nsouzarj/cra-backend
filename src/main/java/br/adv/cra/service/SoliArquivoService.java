package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SoliArquivoService {

    private static final Logger logger = LoggerFactory.getLogger(SoliArquivoService.class);
    
    private final SoliArquivoRepository soliArquivoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final GoogleDriveOAuthService googleDriveOAuthService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${google.drive.oauth.enabled:false}")
    private boolean googleDriveOAuthEnabled;

    // Setter method for testing purposes
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * Save a new file attachment
     *
     * @param file          The file to save
     * @param solicitacaoId The ID of the solicitacao to attach the file to
     * @param origem        The origin of the file (e.g., "correspondente" or user)
     * @return The saved SoliArquivo entity
     * @throws IOException If there's an error saving the file
     */
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem) throws IOException {
    
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> {
                    logger.error("Solicitação with ID {} not found", solicitacaoId);
                    return new RuntimeException("Solicitação não encontrada");
                });
        
        // Check if Google Drive OAuth is enabled and properly initialized
        if (googleDriveOAuthEnabled && googleDriveOAuthService.isInitialized()) {
            // Upload file to Google Drive using OAuth
            logger.info("Uploading file to Google Drive using OAuth...");
            try {
                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                
                String driveFileId = googleDriveOAuthService.uploadFile(file, uniqueFilename);
                
                // Create and save the SoliArquivo entity with Google Drive info
                SoliArquivo soliArquivo = new SoliArquivo();
                soliArquivo.setSolicitacao(solicitacao);
                soliArquivo.setNomearquivo(originalFilename);
                soliArquivo.setDatainclusao(LocalDateTime.now());
                soliArquivo.setCaminhofisico("Google Drive OAuth: " + driveFileId);
                soliArquivo.setOrigem(origem);
                soliArquivo.setAtivo(true);
                soliArquivo.setCaminhorelativo("/api/soli-arquivos/" + soliArquivo.getId() + "/download");
                soliArquivo.setDriveFileId(driveFileId); // Store the Google Drive file ID
                
                logger.info("Saving SoliArquivo entity to database...");
                SoliArquivo saved = soliArquivoRepository.save(soliArquivo);
                logger.info("SoliArquivo entity saved successfully with ID: {}", saved.getId());
                return saved;
            } catch (Exception e) {
                logger.error("Failed to upload file to Google Drive using OAuth: {}", e.getMessage(), e);
                // Fallback to local storage if Google Drive fails
                logger.info("Falling back to local storage...");
                return saveFileLocally(file, solicitacao, origem);
            }
        } else {
            // Save the file to the filesystem (original behavior)
            // This is used when OAuth is not enabled or not properly initialized
            return saveFileLocally(file, solicitacao, origem);
        }
    }
    
    /**
     * Save a file to the local filesystem
     */
    private SoliArquivo saveFileLocally(MultipartFile file, Solicitacao solicitacao, String origem) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            logger.info("Upload directory does not exist, creating it...");
            Files.createDirectories(uploadPath);
        }

        // Generate a unique filename with UUID + original filename
        String originalFilename = file.getOriginalFilename();
        logger.info("Original filename: {}", originalFilename);
        
        String fileExtension = "";
        String fileNameWithoutExtension = originalFilename;
        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            fileExtension = originalFilename.substring(lastDotIndex);
            fileNameWithoutExtension = originalFilename.substring(0, lastDotIndex);
        }
        
        // Create filename as UUID + original filename (without extension) + extension
        String uniqueFilename = UUID.randomUUID().toString() + "_" + fileNameWithoutExtension + fileExtension;
        logger.info("Generated unique filename: {}", uniqueFilename);
        
        Path filePath = uploadPath.resolve(uniqueFilename);
        logger.info("Full file path: {}", filePath);

        // Save the file to the filesystem
        logger.info("Saving file to filesystem...");
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved successfully to filesystem");
        } catch (IOException e) {
            logger.error("Failed to save file to filesystem: {}", e.getMessage(), e);
            throw e;
        }

        // Create and save the SoliArquivo entity
        logger.info("Creating SoliArquivo entity...");
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setSolicitacao(solicitacao);
        soliArquivo.setNomearquivo(originalFilename);
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setCaminhofisico(filePath.toString());
        soliArquivo.setOrigem(origem);
        soliArquivo.setAtivo(true); // Default to active
        soliArquivo.setCaminhorelativo("/arquivos/" + uniqueFilename); // Relative path for HTTP access
        
        logger.info("Saving SoliArquivo entity to database...");
        try {
            SoliArquivo saved = soliArquivoRepository.save(soliArquivo);
            logger.info("SoliArquivo entity saved successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Failed to save SoliArquivo entity to database: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all file attachments for a solicitacao
     *
     * @param solicitacaoId The ID of the solicitacao
     * @return List of file attachments
     */
    @Transactional(readOnly = true)
    public List<SoliArquivo> listarAnexosPorSolicitacao(Long solicitacaoId) {
        return soliArquivoRepository.findBySolicitacaoIdsolicitacao(solicitacaoId);
    }

    /**
     * Get a specific file attachment by ID
     *
     * @param id The ID of the file attachment
     * @return The file attachment if found
     */
    @Transactional(readOnly = true)
    public Optional<SoliArquivo> buscarPorId(Long id) {
        return soliArquivoRepository.findById(id);
    }

    /**
     * Update a file attachment
     *
     * @param id         The ID of the file attachment to update
     * @param soliArquivo The updated file attachment data
     * @return The updated file attachment
     */
    public SoliArquivo atualizar(Long id, SoliArquivo soliArquivo) {
        if (!soliArquivoRepository.existsById(id)) {
            throw new RuntimeException("Arquivo não encontrado");
        }
        soliArquivo.setId(id);
        return soliArquivoRepository.save(soliArquivo);
    }

    /**
     * Delete a file attachment
     *
     * @param id The ID of the file attachment to delete
     */
    public void deletar(Long id) {
        // First get the file to delete the physical file
        SoliArquivo soliArquivo = soliArquivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        // Check if this file was stored in Google Drive using OAuth
        if (googleDriveOAuthEnabled && googleDriveOAuthService.isInitialized() && soliArquivo.getDriveFileId() != null) {
            // Delete the file from Google Drive using OAuth
            try {
                googleDriveOAuthService.deleteFile(soliArquivo.getDriveFileId());
            } catch (IOException e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete file from Google Drive using OAuth: {}", e.getMessage(), e);
            }
        } 
        // Otherwise, it's a local file
        else if (soliArquivo.getCaminhofisico() != null) {
            // Delete the physical file
            try {
                Path filePath = Paths.get(soliArquivo.getCaminhofisico());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete physical file: {}", e.getMessage(), e);
            }
        }

        // Delete the database record
        soliArquivoRepository.deleteById(id);
    }

    /**
     * Check if a file can be deleted by a specific origin
     * For example, a correspondent can only delete their own files
     *
     * @param id     The ID of the file attachment
     * @param origem The origin trying to delete the file
     * @return true if the file can be deleted, false otherwise
     */
    public boolean podeDeletar(Long id, String origem) {
        Optional<SoliArquivo> soliArquivoOpt = soliArquivoRepository.findById(id);
        if (soliArquivoOpt.isPresent()) {
            SoliArquivo soliArquivo = soliArquivoOpt.get();
            // A correspondent can only delete their own files
            if ("correspondente".equals(origem)) {
                return "correspondente".equals(soliArquivo.getOrigem());
            }
            // Other users (like admins) can delete any file
            return true;
        }
        return false;
    }
}