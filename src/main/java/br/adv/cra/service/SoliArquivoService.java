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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
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
    private final GoogleDriveService googleDriveService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${google.drive.oauth.enabled:false}")
    private boolean googleDriveEnabled;

    // Setter method for testing purposes
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * Save an attachment file for a solicitation
     *
     * @param file          The file to save
     * @param solicitacaoId The ID of the solicitation to attach the file to
     * @param origem        The origin of the file (e.g., "correspondente" or user)
     * @param storageLocation Where to store the file ("local" or "google_drive")
     * @return The saved SoliArquivo entity
     * @throws IOException If there's an error saving the file
     */
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem, String storageLocation) throws IOException {
        logger.info("Saving attachment for solicitacao ID: {}", solicitacaoId);
        logger.info("Origin: {}, Storage location: {}, File name: {}, File size: {} bytes", 
                   origem, storageLocation, file.getOriginalFilename(), file.getSize());
    
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> {
                    logger.error("Solicitação with ID {} not found", solicitacaoId);
                    return new RuntimeException("Solicitação não encontrada");
                });
        
        // Determine storage location based on configuration and parameters
        if (googleDriveEnabled && "google_drive".equals(storageLocation)) {
            logger.info("Attempting to save file to Google Drive");
            // For Google Drive storage, we don't fallback to local storage on error
            // We validate authentication and throw an error if it fails
            if (!googleDriveService.hasValidTokens()) {
                logger.error("Google Drive authentication is required but no valid tokens are available");
                throw new IOException("Google Drive authentication is required but not available. Please authenticate with Google Drive first.");
            }
            
            // Check if Google Drive is available before attempting upload
            if (!googleDriveService.isGoogleDriveAvailable()) {
                logger.error("Google Drive is not available or not properly authenticated");
                throw new IOException("Google Drive is not available or not properly authenticated. Please check your connection and authentication.");
            }
            
            // Proceed with Google Drive upload
            return saveFileToGoogleDrive(file, solicitacao, origem);
        } else {
            logger.info("Saving file locally");
            return saveFileLocally(file, solicitacao, origem);
        }
    }
    
    /**
     * Save an attachment file for a solicitation (backward compatibility)
     *
     * @param file          The file to save
     * @param solicitacaoId The ID of the solicitation to attach the file to
     * @param origem        The origin of the file (e.g., "correspondente" or user)
     * @return The saved SoliArquivo entity
     * @throws IOException If there's an error saving the file
     */
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem) throws IOException {
        return salvarAnexo(file, solicitacaoId, origem, "local");
    }
    
    /**
     * Save a file to Google Drive
     */
    private SoliArquivo saveFileToGoogleDrive(MultipartFile file, Solicitacao solicitacao, String origem) throws IOException {
        logger.info("Starting Google Drive file save operation");
        
        // Upload file to Google Drive
        String googleDriveFileId = googleDriveService.uploadFile(file);
        
        // Create and save the SoliArquivo entity
        logger.info("Creating SoliArquivo entity for Google Drive file with ID: {}", googleDriveFileId);
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setSolicitacao(solicitacao);
        soliArquivo.setNomearquivo(file.getOriginalFilename());
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setCaminhofisico(null); // No physical path for Google Drive files
        soliArquivo.setOrigem(origem);
        soliArquivo.setAtivo(true); // Default to active
        soliArquivo.setCaminhorelativo(null); // No relative path for Google Drive files
        soliArquivo.setStorageLocation("google_drive");
        soliArquivo.setGoogleDriveFileId(googleDriveFileId);
        soliArquivo.setUserId(null);
        
        logger.info("Saving SoliArquivo entity to database");
        try {
            SoliArquivo saved = soliArquivoRepository.save(soliArquivo);
            logger.info("SoliArquivo entity saved successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Failed to save SoliArquivo entity to database: {}", e.getMessage(), e);
            // If database save fails, try to delete the file from Google Drive
            try {
                logger.info("Attempting to clean up Google Drive file due to database save failure");
                googleDriveService.deleteFile(googleDriveFileId);
            } catch (IOException deleteException) {
                logger.error("Failed to delete file from Google Drive after database save failure: {}", deleteException.getMessage(), deleteException);
            } catch (Exception deleteException) {
                logger.error("Failed to delete file from Google Drive after database save failure: {}", deleteException.getMessage(), deleteException);
            }
            throw e;
        }
    }
    
    /**
     * Save a file to the local filesystem
     */
    private SoliArquivo saveFileLocally(MultipartFile file, Solicitacao solicitacao, String origem) throws IOException {
        logger.info("Starting local file save operation");
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            logger.info("Upload directory does not exist, creating it: {}", uploadPath);
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
        logger.info("Saving file to filesystem");
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved successfully to filesystem");
        } catch (IOException e) {
            logger.error("Failed to save file to filesystem: {}", e.getMessage(), e);
            throw e;
        }

        // Create and save the SoliArquivo entity
        logger.info("Creating SoliArquivo entity for local file");
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setSolicitacao(solicitacao);
        soliArquivo.setNomearquivo(originalFilename);
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setCaminhofisico(filePath.toString());
        soliArquivo.setOrigem(origem);
        soliArquivo.setAtivo(true); // Default to active
        soliArquivo.setCaminhorelativo("/arquivos/" + uniqueFilename); // Relative path for HTTP access
        soliArquivo.setStorageLocation("local");
        soliArquivo.setGoogleDriveFileId(null);
        soliArquivo.setUserId(null);
        
        logger.info("Saving SoliArquivo entity to database");
        try {
            SoliArquivo saved = soliArquivoRepository.save(soliArquivo);
            logger.info("SoliArquivo entity saved successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Failed to save SoliArquivo entity to database: {}", e.getMessage(), e);
            // If database save fails, try to delete the file from local storage
            try {
                logger.info("Attempting to clean up local file due to database save failure");
                Files.deleteIfExists(filePath);
            } catch (IOException deleteException) {
                logger.error("Failed to delete file from local storage after database save failure: {}", deleteException.getMessage(), deleteException);
            }
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
        logger.info("Listing file attachments for solicitacao ID: {}", solicitacaoId);
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
        logger.info("Searching for file attachment with ID: {}", id);
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
        logger.info("Updating file attachment with ID: {}", id);
        if (!soliArquivoRepository.existsById(id)) {
            logger.error("File attachment with ID {} not found", id);
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
        logger.info("Deleting file attachment with ID: {}", id);
        
        // First get the file to delete the physical file
        SoliArquivo soliArquivo = soliArquivoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("File attachment with ID {} not found", id);
                    return new RuntimeException("Arquivo não encontrado");
                });

        // Delete the physical file based on storage location
        if ("google_drive".equals(soliArquivo.getStorageLocation()) && soliArquivo.getGoogleDriveFileId() != null) {
            // Delete from Google Drive
            try {
                logger.info("Deleting file from Google Drive with ID: {}", soliArquivo.getGoogleDriveFileId());
                googleDriveService.deleteFile(soliArquivo.getGoogleDriveFileId());
                logger.info("File deleted from Google Drive successfully");
            } catch (SocketException e) {
                logger.error("Network error during Google Drive file deletion: ", e);
                throw new RuntimeException("Network error while deleting file from Google Drive", e);
            } catch (IOException e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete file from Google Drive: {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete file from Google Drive: {}", e.getMessage(), e);
            } catch (Exception e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete file from Google Drive: {}", e.getMessage(), e);
            }
        } else if (soliArquivo.getCaminhofisico() != null) {
            // Delete from local storage
            try {
                logger.info("Deleting file from local storage: {}", soliArquivo.getCaminhofisico());
                Path filePath = Paths.get(soliArquivo.getCaminhofisico());
                Files.deleteIfExists(filePath);
                logger.info("File deleted from local storage successfully");
            } catch (IOException e) {
                // Log the error but don't stop the deletion process
                logger.error("Failed to delete physical file: {}", e.getMessage(), e);
            }
        }

        // Delete the database record
        logger.info("Deleting file attachment record from database");
        soliArquivoRepository.deleteById(id);
        logger.info("File attachment record deleted successfully");
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
        logger.info("Checking delete permission for file ID: {} by origin: {}", id, origem);
        Optional<SoliArquivo> soliArquivoOpt = soliArquivoRepository.findById(id);
        if (soliArquivoOpt.isPresent()) {
            SoliArquivo soliArquivo = soliArquivoOpt.get();
            // A correspondent can only delete their own files
            if ("correspondente".equals(origem)) {
                boolean canDelete = "correspondente".equals(soliArquivo.getOrigem());
                logger.info("Correspondent {} delete permission: {}", origem, canDelete);
                return canDelete;
            }
            // Other users (like admins) can delete any file
            logger.info("User {} has permission to delete any file", origem);
            return true;
        }
        logger.warn("File with ID {} not found for permission check", id);
        return false;
    }

    /**
     * Get file content as InputStream from either local storage or Google Drive
     *
     * @param id The ID of the file attachment
     * @return InputStream of the file content
     * @throws IOException If there's an error reading the file
     */
    public InputStream getFileContent(Long id) throws IOException {
        logger.info("Getting file content for file ID: {}", id);
        
        SoliArquivo soliArquivo = soliArquivoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("File attachment with ID {} not found", id);
                    return new RuntimeException("Arquivo não encontrado");
                });
        
        if ("google_drive".equals(soliArquivo.getStorageLocation()) && soliArquivo.getGoogleDriveFileId() != null) {
            // For Google Drive storage, return file from Google Drive
            try {
                logger.info("Downloading file from Google Drive with ID: {}", soliArquivo.getGoogleDriveFileId());
                InputStream result = googleDriveService.downloadFile(soliArquivo.getGoogleDriveFileId());
                logger.info("File downloaded successfully from Google Drive");
                return result;
            } catch (SocketException e) {
                logger.error("Network error during Google Drive file download: ", e);
                throw new IOException("Network error while downloading file from Google Drive", e);
            } catch (RuntimeException e) {
                logger.error("Failed to download file from Google Drive: {}", e.getMessage(), e);
                throw new IOException("Failed to download file from Google Drive: " + e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Failed to download file from Google Drive: {}", e.getMessage(), e);
                throw new IOException("Failed to download file from Google Drive: " + e.getMessage(), e);
            }
        } else {
            // For local storage, return file from filesystem
            logger.info("Reading file from local storage: {}", soliArquivo.getCaminhofisico());
            Path filePath = Paths.get(soliArquivo.getCaminhofisico());
            return new FileInputStream(filePath.toFile());
        }
    }
    
    /**
     * Check if Google Drive is currently available
     * 
     * @return true if Google Drive is available, false otherwise
     */
    public boolean isGoogleDriveAvailable() {
        return googleDriveService.isGoogleDriveAvailable();
    }
}