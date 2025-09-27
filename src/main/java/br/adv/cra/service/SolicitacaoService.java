package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoDTO;
import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import br.adv.cra.specification.SolicitacaoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitacaoService {
    
    private final SolicitacaoRepository solicitacaoRepository;
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    private final SoliArquivoRepository soliArquivoRepository;
    
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
        
        // First delete all related soliArquivo records to avoid foreign key constraint violation
        soliArquivoRepository.deleteAll(soliArquivoRepository.findBySolicitacaoIdsolicitacao(id));
        
        // Then delete all related historico records to avoid foreign key constraint violation
        // We need to do this because there's no HistoricoRepository
        solicitacaoRepository.deleteHistoricoBySolicitacaoId(id);
        
        // Then delete the solicitacao
        solicitacaoRepository.deleteById(id);
    }
    
    public Optional<Solicitacao> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<SolicitacaoDTO> listarTodasDTO() {
        return solicitacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "datasolicitacao")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public SolicitacaoDTO toDTO(Solicitacao solicitacao) {
        SolicitacaoDTO dto = new SolicitacaoDTO();
        dto.setIdsolicitacao(solicitacao.getId());
        dto.setDatasolicitacao(solicitacao.getDatasolicitacao());
        dto.setDataconclusao(solicitacao.getDataconclusao());
        dto.setDataagendamento(solicitacao.getDataagendamento());
        dto.setDataprazo(solicitacao.getDataprazo());
        dto.setNumero(solicitacao.getNumero());
        dto.setVara(solicitacao.getVara());
        dto.setUf(solicitacao.getUf());
        dto.setRequerente(solicitacao.getRequerente());
        dto.setRequerido(solicitacao.getRequerido());
        dto.setObservacao(solicitacao.getObservacao());
        dto.setInstrucoes(solicitacao.getInstrucoes());
        dto.setComplemento(solicitacao.getComplemento());
        dto.setJustificativa(solicitacao.getJustificativa());
        dto.setTratposaudiencia(solicitacao.getTratposaudiencia());
        dto.setNumcontrole(solicitacao.getNumcontrole());
        dto.setTempreposto(solicitacao.isTempreposto());
        dto.setConvolada(solicitacao.isConvolada());
        dto.setHoraudiencia(solicitacao.getHoraudiencia());
        dto.setStatusexterno(solicitacao.getStatusexterno());
        dto.setValor(solicitacao.getValor());
        dto.setValordaalcada(solicitacao.getValordaalcada());
        dto.setEmailenvio(solicitacao.getEmailenvio());
        dto.setPago(solicitacao.getPago());
        dto.setGrupo(solicitacao.getGrupo());
        dto.setPropostaacordo(solicitacao.isPropostaacordo());
        dto.setAudinterna(solicitacao.isAudinterna());
        dto.setLide(solicitacao.getLide());
        dto.setAvaliacaonota(solicitacao.getAvaliacaonota());
        dto.setTextoavaliacao(solicitacao.getTextoavaliacao());
        
        if (solicitacao.getComarca() != null) {
            dto.setComarcaId(solicitacao.getComarca().getId());
            dto.setComarcaNome(solicitacao.getComarca().getNome());
        }
        
        if (solicitacao.getProcesso() != null) {
            dto.setProcessoId(solicitacao.getProcesso().getId());
            dto.setProcessoNumero(solicitacao.getProcesso().getNumeroprocesso());
        }
        
        if (solicitacao.getStatusSolicitacao() != null) {
            dto.setStatusSolicitacaoId(solicitacao.getStatusSolicitacao().getIdstatus());
            dto.setStatusSolicitacaoStatus(solicitacao.getStatusSolicitacao().getStatus());
        }
        
        if (solicitacao.getUsuario() != null) {
            dto.setUsuarioId(solicitacao.getUsuario().getId());
            dto.setUsuarioNome(solicitacao.getUsuario().getNomecompleto());
        }
        
        if (solicitacao.getTipoSolicitacao() != null) {
            dto.setTipoSolicitacaoId(solicitacao.getTipoSolicitacao().getIdtiposolicitacao());
            dto.setTipoSolicitacaoEspecie(solicitacao.getTipoSolicitacao().getEspecie());
        }
        
        if (solicitacao.getCorrespondente() != null) {
            dto.setCorrespondenteId(solicitacao.getCorrespondente().getId());
            dto.setCorrespondenteNome(solicitacao.getCorrespondente().getNome());
        }
        
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarTodas(Sort sort) {
        return solicitacaoRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarTodas(Pageable pageable) {
        return solicitacaoRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<SolicitacaoDTO> listarTodasDTO(Pageable pageable) {
        Page<Solicitacao> solicitacoes = solicitacaoRepository.findAll(pageable);
        return solicitacoes.map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public long contarTodas() {
        return solicitacaoRepository.countAllSolicitacoes();
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorUsuario(Usuario usuario) {
        return solicitacaoRepository.findByUsuario(usuario, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorUsuario(Usuario usuario, Sort sort) {
        return solicitacaoRepository.findByUsuario(usuario, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorUsuario(Usuario usuario, Pageable pageable) {
        return solicitacaoRepository.findByUsuario(usuario, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorProcesso(Processo processo) {
        return solicitacaoRepository.findByProcesso(processo, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorProcesso(Processo processo, Sort sort) {
        return solicitacaoRepository.findByProcesso(processo, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorProcesso(Processo processo, Pageable pageable) {
        return solicitacaoRepository.findByProcesso(processo, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorComarca(Comarca comarca) {
        return solicitacaoRepository.findByComarca(comarca, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorComarca(Comarca comarca, Sort sort) {
        return solicitacaoRepository.findByComarca(comarca, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorComarca(Comarca comarca, Pageable pageable) {
        return solicitacaoRepository.findByComarca(comarca, pageable);
    }
    
    // Adding method to find solicitacoes by comarca and correspondente
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorComarcaECorrespondente(Comarca comarca, Correspondente correspondente, Pageable pageable) {
        return solicitacaoRepository.findByComarcaAndCorrespondente(comarca, correspondente, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorCorrespondente(Correspondente correspondente) {
        return solicitacaoRepository.findByCorrespondente(correspondente, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorCorrespondente(Correspondente correspondente, Sort sort) {
        return solicitacaoRepository.findByCorrespondente(correspondente, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorCorrespondente(Correspondente correspondente, Pageable pageable) {
        return solicitacaoRepository.findByCorrespondente(correspondente, pageable);
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
        return solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorUsuarioECorrespondente(Usuario usuario, Correspondente correspondente, Sort sort) {
        return solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorUsuarioECorrespondente(Usuario usuario, Correspondente correspondente, Pageable pageable) {
        return solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return solicitacaoRepository.findByDatasolicitacaoBetween(inicio, fim, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Sort sort) {
        return solicitacaoRepository.findByDatasolicitacaoBetween(inicio, fim, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return solicitacaoRepository.findByDatasolicitacaoBetween(inicio, fim, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPendentes() {
        return solicitacaoRepository.findPendentes(Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPendentes(Sort sort) {
        return solicitacaoRepository.findPendentes(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarPendentes(Pageable pageable) {
        return solicitacaoRepository.findPendentes(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarConcluidas() {
        return solicitacaoRepository.findConcluidas(Sort.by(Sort.Direction.DESC, "dataconclusao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarConcluidas(Sort sort) {
        return solicitacaoRepository.findConcluidas(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarConcluidas(Pageable pageable) {
        return solicitacaoRepository.findConcluidas(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPagas() {
        return solicitacaoRepository.findByPagoTrue(Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarPagas(Sort sort) {
        return solicitacaoRepository.findByPagoTrue(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarPagas(Pageable pageable) {
        return solicitacaoRepository.findByPagoTrue(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarNaoPagas() {
        return solicitacaoRepository.findByPagoFalse(Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarNaoPagas(Sort sort) {
        return solicitacaoRepository.findByPagoFalse(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarNaoPagas(Pageable pageable) {
        return solicitacaoRepository.findByPagoFalse(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarAtrasadas() {
        return solicitacaoRepository.findAtrasadas(LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "dataprazo"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> listarAtrasadas(Sort sort) {
        return solicitacaoRepository.findAtrasadas(LocalDateTime.now(), sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> listarAtrasadas(Pageable pageable) {
        return solicitacaoRepository.findAtrasadas(LocalDateTime.now(), pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorTexto(String texto) {
        return solicitacaoRepository.findByTextoContaining(texto, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorTexto(String texto, Sort sort) {
        return solicitacaoRepository.findByTextoContaining(texto, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorTexto(String texto, Pageable pageable) {
        return solicitacaoRepository.findByTextoContaining(texto, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorGrupo(Integer grupo) {
        return solicitacaoRepository.findByGrupo(grupo, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorGrupo(Integer grupo, Sort sort) {
        return solicitacaoRepository.findByGrupo(grupo, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorGrupo(Integer grupo, Pageable pageable) {
        return solicitacaoRepository.findByGrupo(grupo, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorStatusExterno(String statusexterno) {
        return solicitacaoRepository.findByStatusexterno(statusexterno, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
    }
    
    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorStatusExterno(String statusexterno, Sort sort) {
        return solicitacaoRepository.findByStatusexterno(statusexterno, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarPorStatusExterno(String statusexterno, Pageable pageable) {
        return solicitacaoRepository.findByStatusexterno(statusexterno, pageable);
    }
    
    /**
     * Advanced search method that handles multiple combined filters from SolicitacaoFiltroDTO
     * 
     * @param filtro The filter criteria
     * @param pageable Pagination information
     * @return Page of solicitacoes matching all provided filter criteria
     */
    @Transactional(readOnly = true)
    public Page<Solicitacao> buscarAvancado(SolicitacaoFiltroDTO filtro, Pageable pageable) {
        // Build specification based on provided filters
        Specification<Solicitacao> spec = Specification.where(null);
        
        // Add filters to specification
        spec = spec.and(SolicitacaoSpecification.comarcaIdEquals(filtro.getComarcaId()));
        spec = spec.and(SolicitacaoSpecification.correspondenteIdEquals(filtro.getCorrespondenteId()));
        spec = spec.and(SolicitacaoSpecification.processoIdEquals(filtro.getProcessoId()));
        spec = spec.and(SolicitacaoSpecification.usuarioIdEquals(filtro.getUsuarioId()));
        spec = spec.and(SolicitacaoSpecification.statusIdEquals(filtro.getStatusId()));
        spec = spec.and(SolicitacaoSpecification.statusEquals(filtro.getStatus())); // Add status by name filter
        spec = spec.and(SolicitacaoSpecification.grupoEquals(filtro.getGrupo()));
        spec = spec.and(SolicitacaoSpecification.statusExternoEquals(filtro.getStatusExterno()));
        spec = spec.and(SolicitacaoSpecification.textoContains(filtro.getTexto()));
        spec = spec.and(SolicitacaoSpecification.dataBetween(filtro.getDataInicio(), filtro.getDataFim()));
        spec = spec.and(SolicitacaoSpecification.dataConclusaoBetween(filtro.getDataConclusaoInicio(), filtro.getDataConclusaoFim()));
        spec = spec.and(SolicitacaoSpecification.dataPrazoBetween(filtro.getDataPrazoInicio(), filtro.getDataPrazoFim()));
        spec = spec.and(SolicitacaoSpecification.pagoEquals(filtro.getPago()));
        spec = spec.and(SolicitacaoSpecification.concluidaEquals(filtro.getConcluida()));
        spec = spec.and(SolicitacaoSpecification.atrasadaEquals(filtro.getAtrasada()));
        spec = spec.and(SolicitacaoSpecification.numeroContains(filtro.getNumero()));
        spec = spec.and(SolicitacaoSpecification.varaContains(filtro.getVara()));
        spec = spec.and(SolicitacaoSpecification.requerenteContains(filtro.getRequerente()));
        spec = spec.and(SolicitacaoSpecification.requeridoContains(filtro.getRequerido()));
        spec = spec.and(SolicitacaoSpecification.ufEquals(filtro.getUf()));
        
        // Execute query with specification
        return solicitacaoRepository.findAll(spec, pageable);
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