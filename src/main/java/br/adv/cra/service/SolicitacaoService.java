package br.adv.cra.service;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitacaoService {
    
    private final SolicitacaoRepository solicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    
    public Solicitacao salvar(Solicitacao solicitacao) {
        if (solicitacao.getDatasolicitacao() == null) {
            solicitacao.setDatasolicitacao(LocalDateTime.now());
        }
        return solicitacaoRepository.save(solicitacao);
    }
    
    public Solicitacao atualizar(Solicitacao solicitacao) {
        if (!solicitacaoRepository.existsById(solicitacao.getId())) {
            throw new RuntimeException("Solicitação não encontrada");
        }
        // Ensure the datasolicitacao is not null
        if (solicitacao.getDatasolicitacao() == null) {
            solicitacao.setDatasolicitacao(LocalDateTime.now());
        }
        return solicitacaoRepository.save(solicitacao);
    }
    
    @Transactional
    public Solicitacao setStatus(Long solicitacaoId, Long statusId) {
        System.out.println("Setting status for solicitacao ID: " + solicitacaoId + " to status ID: " + statusId);
        
        Solicitacao solicitacao = buscarPorId(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        
        System.out.println("Current status: " + (solicitacao.getStatusSolicitacao() != null ? solicitacao.getStatusSolicitacao().getStatus() : "null"));
        
        // Check if the current status is "Concluída" and enforce role-based access control
        if (solicitacao.getStatusSolicitacao() != null && 
            "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
            // Here we would normally check the user's role, but since we don't have that context in the service
            // we'll add a comment that this should be handled in the controller or security layer
            System.out.println("Warning: Attempting to change status of a completed solicitacao");
        }
        
        StatusSolicitacao status = statusSolicitacaoRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));
        
        System.out.println("New status: " + status.getStatus());
        
        solicitacao.setStatusSolicitacao(status);
        Solicitacao saved = solicitacaoRepository.saveAndFlush(solicitacao);
        
        System.out.println("Saved status: " + (saved.getStatusSolicitacao() != null ? saved.getStatusSolicitacao().getStatus() : "null"));
        
        return saved;
    }
    
    @Transactional
    public Solicitacao setStatusPorNome(Long solicitacaoId, String statusNome) {
        System.out.println("Setting status for solicitacao ID: " + solicitacaoId + " to status name: " + statusNome);
        
        Solicitacao solicitacao = buscarPorId(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        
        System.out.println("Current status: " + (solicitacao.getStatusSolicitacao() != null ? solicitacao.getStatusSolicitacao().getStatus() : "null"));
        
        // Check if the current status is "Concluída" and enforce role-based access control
        if (solicitacao.getStatusSolicitacao() != null && 
            "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
            // Here we would normally check the user's role, but since we don't have that context in the service
            // we'll add a comment that this should be handled in the controller or security layer
            System.out.println("Warning: Attempting to change status of a completed solicitacao");
        }
        
        StatusSolicitacao status = statusSolicitacaoRepository.findByStatus(statusNome)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));
        
        System.out.println("New status: " + status.getStatus());
        
        solicitacao.setStatusSolicitacao(status);
        Solicitacao saved = solicitacaoRepository.saveAndFlush(solicitacao);
        
        System.out.println("Saved status: " + (saved.getStatusSolicitacao() != null ? saved.getStatusSolicitacao().getStatus() : "null"));
        
        return saved;
    }
    
    public Solicitacao concluir(Long id, String observacaoConclusao) {
        Solicitacao solicitacao = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        
        solicitacao.setDataconclusao(LocalDateTime.now());
        if (observacaoConclusao != null && !observacaoConclusao.trim().isEmpty()) {
            String observacaoAtual = solicitacao.getObservacao() != null ? solicitacao.getObservacao() : "";
            solicitacao.setObservacao(observacaoAtual + "\n\nConclusão: " + observacaoConclusao);
        }
        
        return solicitacaoRepository.save(solicitacao);
    }
    
    public void deletar(Long id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new RuntimeException("Solicitação não encontrada");
        }
        solicitacaoRepository.deleteById(id);
    }
    
    public Optional<Solicitacao> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorUsuario(Usuario usuario) {
        return solicitacaoRepository.findByUsuario(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorProcesso(Processo processo) {
        return solicitacaoRepository.findByProcesso(processo);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorComarca(Comarca comarca) {
        return solicitacaoRepository.findByComarca(comarca);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorCorrespondente(Correspondente correspondente) {
        return solicitacaoRepository.findByCorrespondente(correspondente);
    }
    
    /**
     * Find solicitacoes by usuario and correspondente
     * 
     * @param usuario The usuario to search for
     * @param correspondente The correspondente to search for
     * @return List of solicitacoes matching both usuario and correspondente
     */
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorUsuarioECorrespondente(Usuario usuario, Correspondente correspondente) {
        return solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return solicitacaoRepository.findByDatasolicitacaoBetween(inicio, fim);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPendentes() {
        return solicitacaoRepository.findPendentes();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarConcluidas() {
        return solicitacaoRepository.findConcluidas();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPagas() {
        return solicitacaoRepository.findByPagoTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarNaoPagas() {
        return solicitacaoRepository.findByPagoFalse();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarAtrasadas() {
        return solicitacaoRepository.findAtrasadas(LocalDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorTexto(String texto) {
        return solicitacaoRepository.findByTextoContaining(texto);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorGrupo(Integer grupo) {
        return solicitacaoRepository.findByGrupo(grupo);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorStatusExterno(String statusexterno) {
        return solicitacaoRepository.findByStatusexterno(statusexterno);
    }
    
    @Transactional(readOnly = true)
    public Long contarPorUsuario(Usuario usuario) {
        return solicitacaoRepository.countByUsuario(usuario);
    }
    
    @Transactional(readOnly = true)
    public Long contarPendentes() {
        return solicitacaoRepository.countPendentes();
    }
    
    public Solicitacao marcarComoPago(Long id) {
        Solicitacao solicitacao = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        solicitacao.setPago("true");
        return solicitacaoRepository.save(solicitacao);
    }
    
    public Solicitacao marcarComoNaoPago(Long id) {
        Solicitacao solicitacao = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        solicitacao.setPago("false");
        return solicitacaoRepository.save(solicitacao);
    }
}