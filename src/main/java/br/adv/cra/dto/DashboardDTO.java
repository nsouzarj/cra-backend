package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // General statistics
    private long totalSolicitacoes;
    private long totalProcessos;
    private long totalComarcas;
    private long totalUsuarios;
    
    // Solicitacao statistics
    private long pendentes;
    private long concluidas;
    private long pagas;
    private long naoPagas;
    private long atrasadas;
    
    // Process statistics
    private long processosAtivos;
    private long processosConcluidos;
    
    // Correspondent statistics
    private long totalCorrespondentes;
    private long correspondentesAtivos;
    private long correspondentesInativos;
    
    // Comarca statistics
    private long comarcasAtivas;
    private long comarcasInativas;
    
    // Correspondent ID for correspondent-specific dashboards
    private Long correspondenteId;
    
    // Status statistics
    private List<StatusSolicitacaoCountDTO> solicitacoesPorStatus;
    
    // Top comarcas
    private List<ComarcaCountDTO> topComarcas;
    
    // Solicitacoes by type
    private List<TipoSolicitacaoCountDTO> solicitacoesPorTipo;
    
    // Recent activities
    private List<SolicitacaoDTO> recentSolicitacoes;
}