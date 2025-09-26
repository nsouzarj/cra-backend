package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SoliArquivoService {

    
    private final SoliArquivoRepository soliArquivoRepository;
    
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
    
    // Added missing methods that the controller is trying to call
    
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem, String storageLocation) throws IOException {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for saving attachments
        throw new UnsupportedOperationException("Method salvarAnexo not yet implemented");
    }
    
    // Backward compatibility method
    public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem) throws IOException {
        // Default to local storage for backward compatibility
        return salvarAnexo(file, solicitacaoId, origem, "local");
    }
    
    public InputStream getFileContent(Long id) throws IOException {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for retrieving file content
        throw new UnsupportedOperationException("Method getFileContent not yet implemented");
    }
    
    public List<SoliArquivo> listarAnexosPorSolicitacao(Long solicitacaoId, Sort sort) {
        return soliArquivoRepository.findBySolicitacaoIdsolicitacao(solicitacaoId, sort);
    }
    
    public boolean fileExists(Long id) {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for checking if a file exists
        return soliArquivoRepository.existsById(id);
    }
    
    public boolean podeDeletar(Long id, String origem) {
        // This is a placeholder implementation
        // You'll need to implement the actual logic for checking delete permissions
        return true; // Allow deletion by default for now
    }
}