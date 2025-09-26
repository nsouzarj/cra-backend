package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SoliArquivoService {

    
    private final SoliArquivoRepository soliArquivoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    
    public SoliArquivo salvar(SoliArquivo soliArquivo) {
        return soliArquivoRepository.save(soliArquivo);
    }
    
    public SoliArquivo atualizar(SoliArquivo soliArquivo) {
        if (!soliArquivoRepository.existsById(soliArquivo.getId())) {
            throw new RuntimeException("Arquivo não encontrado");
        }
        return soliArquivoRepository.save(soliArquivo);
    }
    
    // Added method for updating with ID and entity
    public SoliArquivo atualizar(Long id, SoliArquivo soliArquivo) {
        if (!soliArquivoRepository.existsById(id)) {
            throw new RuntimeException("Arquivo não encontrado");
        }
        soliArquivo.setId(id);
        return soliArquivoRepository.save(soliArquivo);
    }
    
    public void deletar(Long id) {
        if (!soliArquivoRepository.existsById(id)) {
            throw new RuntimeException("Arquivo não encontrado");
        }
        soliArquivoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<SoliArquivo> buscarPorId(Long id) {
        return soliArquivoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<SoliArquivo> listarTodos() {
        return soliArquivoRepository.findAll(Sort.by(Sort.Direction.DESC, "datainclusao"));
    }
    
    @Transactional(readOnly = true)
    public List<SoliArquivo> listarTodos(Sort sort) {
        return soliArquivoRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public List<SoliArquivo> buscarPorSolicitacao(Solicitacao solicitacao) {
        return soliArquivoRepository.findBySolicitacao(solicitacao, Sort.by(Sort.Direction.DESC, "datainclusao"));
    }
    
    @Transactional(readOnly = true)
    public List<SoliArquivo> buscarPorSolicitacao(Solicitacao solicitacao, Sort sort) {
        return soliArquivoRepository.findBySolicitacao(solicitacao, sort);
    }
    
    // Implementation of the missing methods
    
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem, String storageLocation) throws IOException {
        // Find the solicitacao
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        
        // Create a new SoliArquivo entity
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setSolicitacao(solicitacao);
        soliArquivo.setNomearquivo(file.getOriginalFilename());
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setOrigem(origem);
        soliArquivo.setAtivo(true);
        soliArquivo.setStorageLocation(storageLocation);
        
        // Save the entity first to get an ID
        soliArquivo = soliArquivoRepository.save(soliArquivo);
        
        return soliArquivo;
    }
    
    // Backward compatibility method
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem) throws IOException {
        // Default to local storage for backward compatibility
        return salvarAnexo(file, solicitacaoId, origem, "local");
    }
    
    public InputStream getFileContent(Long id) throws IOException {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for retrieving file content based on storage location
        throw new UnsupportedOperationException("Method getFileContent not yet implemented");
    }
    
    public List<SoliArquivo> listarAnexosPorSolicitacao(Long solicitacaoId, Sort sort) {
        return soliArquivoRepository.findBySolicitacaoIdsolicitacao(solicitacaoId, sort);
    }
    
    public boolean fileExists(Long id) {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for checking if a file exists based on storage location
        return soliArquivoRepository.existsById(id);
    }
    
    public boolean podeDeletar(Long id, String origem) {
        // Check if the file exists
        Optional<SoliArquivo> soliArquivoOpt = soliArquivoRepository.findById(id);
        if (!soliArquivoOpt.isPresent()) {
            return false;
        }
        
        SoliArquivo soliArquivo = soliArquivoOpt.get();
        
        // Admins can delete any file
        if ("admin".equals(origem)) {
            return true;
        }
        
        // Users can delete their own files
        if ("usuario".equals(origem) && "usuario".equals(soliArquivo.getOrigem())) {
            return true;
        }
        
        // Correspondentes can delete their own files
        if ("correspondente".equals(origem) && "correspondente".equals(soliArquivo.getOrigem())) {
            return true;
        }
        
        // Default: cannot delete
        return false;
    }
}