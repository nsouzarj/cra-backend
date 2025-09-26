package br.adv.cra.service;

import br.adv.cra.dto.*;
import br.adv.cra.entity.*;
import br.adv.cra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private ProcessoRepository processoRepository;

    @Autowired
    private ComarcaRepository comarcaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoSolicitacaoRepository tipoSolicitacaoRepository;

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private CorrespondenteRepository correspondenteRepository;

    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();

        // General statistics
        dashboard.setTotalSolicitacoes(solicitacaoRepository.countAllSolicitacoes());
        dashboard.setTotalProcessos(processoRepository.countAllProcessos());
        dashboard.setTotalComarcas(comarcaRepository.countAllComarcas());
        dashboard.setTotalUsuarios(usuarioRepository.count());
        
        // Comarca statistics
        dashboard.setComarcasAtivas(comarcaRepository.countAtivas());
        dashboard.setComarcasInativas(comarcaRepository.countInativas());

        // Solicitacao statistics
        dashboard.setPendentes(solicitacaoRepository.countPendentes());
        dashboard.setConcluidas(solicitacaoRepository.count() - solicitacaoRepository.countPendentes());
        dashboard.setPagas(solicitacaoRepository.findByPagoTrue().size());
        dashboard.setNaoPagas(solicitacaoRepository.findByPagoFalse().size());
        dashboard.setAtrasadas(solicitacaoRepository.findAtrasadas(LocalDateTime.now()).size());

        // Process statistics
        dashboard.setProcessosAtivos(processoRepository.countByStatus("Ativo") != null ? processoRepository.countByStatus("Ativo") : 0L);
        dashboard.setProcessosConcluidos(processoRepository.countByStatus("Concluído") != null ? processoRepository.countByStatus("Concluído") : 0L);

        // Correspondent statistics
        List<Usuario> correspondentes = usuarioRepository.findByTipo(3);
        dashboard.setTotalCorrespondentes(correspondentes.size());
        dashboard.setCorrespondentesAtivos(correspondentes.stream().filter(Usuario::isAtivo).count());
        dashboard.setCorrespondentesInativos(correspondentes.stream().filter(u -> !u.isAtivo()).count());

        // Status statistics
        dashboard.setSolicitacoesPorStatus(getSolicitacoesPorStatus());

        // Top comarcas (top 5)
        dashboard.setTopComarcas(getTopComarcas());

        // Solicitacoes by type
        dashboard.setSolicitacoesPorTipo(getSolicitacoesPorTipo());

        // Recent solicitacoes (last 5)
        dashboard.setRecentSolicitacoes(getRecentSolicitacoes());

        return dashboard;
    }

    public DashboardDTO getDashboardDataForCorrespondente(Long correspondenteId) {
        // First, check if the correspondente exists
        Correspondente correspondente = correspondenteRepository.findById(correspondenteId)
                .orElseThrow(() -> new RuntimeException("Correspondente not found with ID: " + correspondenteId));

        DashboardDTO dashboard = new DashboardDTO();
        
        // Set the correspondent ID in the dashboard
        dashboard.setCorrespondenteId(correspondenteId);

        // Correspondent-specific statistics
        dashboard.setTotalSolicitacoes(solicitacaoRepository.countByCorrespondente(correspondente));
        // Note: We're not setting totalProcessos, totalComarcas, totalUsuarios as these are global stats
        // that shouldn't be filtered by correspondent

        // Solicitacao statistics for this correspondent
        dashboard.setPendentes(solicitacaoRepository.countPendentesByCorrespondente(correspondente));
        dashboard.setConcluidas(solicitacaoRepository.countByCorrespondente(correspondente) - solicitacaoRepository.countPendentesByCorrespondente(correspondente));
        dashboard.setPagas(solicitacaoRepository.countPagasByCorrespondente(correspondente).intValue());
        dashboard.setNaoPagas(solicitacaoRepository.countNaoPagasByCorrespondente(correspondente).intValue());
        dashboard.setAtrasadas(solicitacaoRepository.countAtrasadasByCorrespondente(correspondente, LocalDateTime.now()).intValue());

        // Status statistics for this correspondent
        dashboard.setSolicitacoesPorStatus(getSolicitacoesPorStatusForCorrespondente(correspondente));

        // Top comarcas for this correspondent (top 5)
        dashboard.setTopComarcas(getTopComarcasForCorrespondente(correspondente));

        // Solicitacoes by type for this correspondent
        dashboard.setSolicitacoesPorTipo(getSolicitacoesPorTipoForCorrespondente(correspondente));

        // Recent solicitacoes for this correspondent (last 5)
        dashboard.setRecentSolicitacoes(getRecentSolicitacoesForCorrespondente(correspondente));

        return dashboard;
    }

    private List<StatusSolicitacaoCountDTO> getSolicitacoesPorStatus() {
        List<Object[]> results = solicitacaoRepository.countByStatusSolicitacao();
        return results.stream()
                .map(result -> {
                    StatusSolicitacaoCountDTO dto = new StatusSolicitacaoCountDTO();
                    dto.setStatus((String) result[0]);
                    dto.setCount((Long) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<StatusSolicitacaoCountDTO> getSolicitacoesPorStatusForCorrespondente(Correspondente correspondente) {
        List<Object[]> results = solicitacaoRepository.countByStatusSolicitacaoAndCorrespondente(correspondente);
        return results.stream()
                .map(result -> {
                    StatusSolicitacaoCountDTO dto = new StatusSolicitacaoCountDTO();
                    dto.setStatus((String) result[0]);
                    dto.setCount((Long) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<ComarcaCountDTO> getTopComarcas() {
        List<Object[]> results = solicitacaoRepository.countByComarca();
        return results.stream()
                .limit(5)
                .map(result -> {
                    ComarcaCountDTO dto = new ComarcaCountDTO();
                    dto.setComarcaId((Long) result[0]);
                    dto.setComarcaNome((String) result[1]);
                    dto.setCount((Long) result[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<ComarcaCountDTO> getTopComarcasForCorrespondente(Correspondente correspondente) {
        List<Object[]> results = solicitacaoRepository.countByComarcaAndCorrespondente(correspondente);
        return results.stream()
                .limit(5)
                .map(result -> {
                    ComarcaCountDTO dto = new ComarcaCountDTO();
                    dto.setComarcaId((Long) result[0]);
                    dto.setComarcaNome((String) result[1]);
                    dto.setCount((Long) result[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<TipoSolicitacaoCountDTO> getSolicitacoesPorTipo() {
        List<Object[]> results = solicitacaoRepository.countByTipoSolicitacao();
        return results.stream()
                .map(result -> {
                    TipoSolicitacaoCountDTO dto = new TipoSolicitacaoCountDTO();
                    dto.setTipoSolicitacaoId((Long) result[0]);
                    dto.setTipoSolicitacaoEspecie((String) result[1]);
                    dto.setCount((Long) result[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<TipoSolicitacaoCountDTO> getSolicitacoesPorTipoForCorrespondente(Correspondente correspondente) {
        List<Object[]> results = solicitacaoRepository.countByTipoSolicitacaoAndCorrespondente(correspondente);
        return results.stream()
                .map(result -> {
                    TipoSolicitacaoCountDTO dto = new TipoSolicitacaoCountDTO();
                    dto.setTipoSolicitacaoId((Long) result[0]);
                    dto.setTipoSolicitacaoEspecie((String) result[1]);
                    dto.setCount((Long) result[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<SolicitacaoDTO> getRecentSolicitacoes() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
        return solicitacaoRepository.findAll(pageable).getContent().stream()
                .map(solicitacaoService::toDTO)
                .collect(Collectors.toList());
    }

    private List<SolicitacaoDTO> getRecentSolicitacoesForCorrespondente(Correspondente correspondente) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "datasolicitacao"));
        return solicitacaoRepository.findByCorrespondente(correspondente, pageable).getContent().stream()
                .map(solicitacaoService::toDTO)
                .collect(Collectors.toList());
    }
}