package br.adv.cra.service;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.SolicitacaoAnexo;
import br.adv.cra.entity.SolicitacaoPossuiArquivo;
import br.adv.cra.entity.SolicitacaoPossuiArquivoId;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.SolicitacaoAnexoRepository;
import br.adv.cra.repository.SolicitacaoPossuiArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
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
public class SolicitacaoAnexoService {

    private final SolicitacaoAnexoRepository solicitacaoAnexoRepository;
    private final SolicitacaoPossuiArquivoRepository solicitacaoPossuiArquivoRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Setter method for testing purposes
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * Save a new file attachment
     *
     * @param file     The file to save
     * @param solicitacaoId The ID of the solicitacao to attach the file to
     * @param usuario  The user uploading the file
     * @return The saved SolicitacaoAnexo entity
     * @throws IOException If there's an error saving the file
     */
    public SolicitacaoAnexo salvarAnexo(MultipartFile file, Long solicitacaoId, Usuario usuario) throws IOException {
        // Verify that the solicitacao exists
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Create the directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Save the file to the filesystem
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create and save the SolicitacaoAnexo entity
        SolicitacaoAnexo anexo = new SolicitacaoAnexo();
        anexo.setLinkarquivo(filePath.toString());
        anexo.setNomearquivo(originalFilename);
        anexo.setTipoarquivo(file.getContentType());
        anexo.setDatasolicitacao(LocalDateTime.now());
        anexo.setUsuario(usuario);
        anexo.setOperacao("Entrada"); // Default to entrada
        anexo.setOrigemarq(1); // Default origin

        SolicitacaoAnexo savedAnexo = solicitacaoAnexoRepository.save(anexo);

        // Create the relationship between solicitacao and anexo
        SolicitacaoPossuiArquivoId id = new SolicitacaoPossuiArquivoId();
        id.setSolicitacao(solicitacaoId);
        id.setSolicitacaoAnexo(savedAnexo.getIdarquivoanexo());
        
        SolicitacaoPossuiArquivo solicitacaoPossuiArquivo = new SolicitacaoPossuiArquivo();
        solicitacaoPossuiArquivo.setSolicitacao(solicitacao);
        solicitacaoPossuiArquivo.setSolicitacaoAnexo(savedAnexo);

        solicitacaoPossuiArquivoRepository.save(solicitacaoPossuiArquivo);

        return savedAnexo;
    }

    /**
     * Get all file attachments for a solicitacao
     *
     * @param solicitacaoId The ID of the solicitacao
     * @return List of file attachments
     */
    @Transactional(readOnly = true)
    public List<SolicitacaoAnexo> listarAnexosPorSolicitacao(Long solicitacaoId) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);
        List<SolicitacaoPossuiArquivo> relationships = solicitacaoPossuiArquivoRepository.findBySolicitacao(solicitacao);
        return relationships.stream()
                .map(SolicitacaoPossuiArquivo::getSolicitacaoAnexo)
                .toList();
    }

    /**
     * Get a specific file attachment by ID
     *
     * @param id The ID of the file attachment
     * @return The file attachment if found
     */
    @Transactional(readOnly = true)
    public Optional<SolicitacaoAnexo> buscarPorId(Long id) {
        return solicitacaoAnexoRepository.findById(id);
    }

    /**
     * Update a file attachment
     *
     * @param id     The ID of the file attachment to update
     * @param anexo  The updated file attachment data
     * @return The updated file attachment
     */
    public SolicitacaoAnexo atualizar(Long id, SolicitacaoAnexo anexo) {
        if (!solicitacaoAnexoRepository.existsById(id)) {
            throw new RuntimeException("Anexo não encontrado");
        }
        anexo.setIdarquivoanexo(id);
        return solicitacaoAnexoRepository.save(anexo);
    }

    /**
     * Delete a file attachment
     *
     * @param id The ID of the file attachment to delete
     */
    public void deletar(Long id) {
        // First delete the relationship
        SolicitacaoAnexo anexo = solicitacaoAnexoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));

        // Delete the physical file
        try {
            Path filePath = Paths.get(anexo.getLinkarquivo());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log the error but don't stop the deletion process
            e.printStackTrace();
        }

        // Delete the relationship records
        SolicitacaoAnexo solicitacaoAnexo = new SolicitacaoAnexo();
        solicitacaoAnexo.setIdarquivoanexo(id);
        solicitacaoPossuiArquivoRepository.deleteBySolicitacaoAnexo(solicitacaoAnexo);

        // Finally delete the anexo record
        solicitacaoAnexoRepository.deleteById(id);
    }
}