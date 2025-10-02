package br.adv.cra.service;

import br.adv.cra.dto.*;
import br.adv.cra.entity.*;
import br.adv.cra.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private ComarcaRepository comarcaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoSolicitacaoRepository tipoSolicitacaoRepository;

    @Mock
    private SolicitacaoService solicitacaoService;

    @Mock
    private CorrespondenteRepository correspondenteRepository;

    private DashboardService dashboardService;

    private Correspondente testCorrespondente;
    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardService = new DashboardService();
        
        // Use reflection to inject mocks since DashboardService uses @Autowired
        try {
            java.lang.reflect.Field solicitacaoRepositoryField = DashboardService.class.getDeclaredField("solicitacaoRepository");
            solicitacaoRepositoryField.setAccessible(true);
            solicitacaoRepositoryField.set(dashboardService, solicitacaoRepository);
            
            java.lang.reflect.Field processoRepositoryField = DashboardService.class.getDeclaredField("processoRepository");
            processoRepositoryField.setAccessible(true);
            processoRepositoryField.set(dashboardService, processoRepository);
            
            java.lang.reflect.Field comarcaRepositoryField = DashboardService.class.getDeclaredField("comarcaRepository");
            comarcaRepositoryField.setAccessible(true);
            comarcaRepositoryField.set(dashboardService, comarcaRepository);
            
            java.lang.reflect.Field usuarioRepositoryField = DashboardService.class.getDeclaredField("usuarioRepository");
            usuarioRepositoryField.setAccessible(true);
            usuarioRepositoryField.set(dashboardService, usuarioRepository);
            
            java.lang.reflect.Field tipoSolicitacaoRepositoryField = DashboardService.class.getDeclaredField("tipoSolicitacaoRepository");
            tipoSolicitacaoRepositoryField.setAccessible(true);
            tipoSolicitacaoRepositoryField.set(dashboardService, tipoSolicitacaoRepository);
            
            java.lang.reflect.Field solicitacaoServiceField = DashboardService.class.getDeclaredField("solicitacaoService");
            solicitacaoServiceField.setAccessible(true);
            solicitacaoServiceField.set(dashboardService, solicitacaoService);
            
            java.lang.reflect.Field correspondenteRepositoryField = DashboardService.class.getDeclaredField("correspondenteRepository");
            correspondenteRepositoryField.setAccessible(true);
            correspondenteRepositoryField.set(dashboardService, correspondenteRepository);
        } catch (Exception e) {
            fail("Failed to inject mocks via reflection: " + e.getMessage());
        }

        // Create test correspondente
        testCorrespondente = new Correspondente();
        testCorrespondente.setId(1L);
        testCorrespondente.setNome("Test Correspondente");

        // Create test usuario
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setNomecompleto("Test User");
        testUsuario.setTipo(3); // Correspondent type
        testUsuario.setAtivo(true);
    }

    @Test
    void testGetDashboardData_ShouldReturnCompleteDashboardData() {
        // Given
        when(solicitacaoRepository.countPendentes()).thenReturn(10L);
        when(solicitacaoRepository.count()).thenReturn(100L);
        when(solicitacaoRepository.findByPagoTrue()).thenReturn(Arrays.asList(new Solicitacao()));
        when(solicitacaoRepository.findByPagoFalse()).thenReturn(Arrays.asList(new Solicitacao(), new Solicitacao()));
        when(solicitacaoRepository.findAtrasadas(any(LocalDateTime.class))).thenReturn(Arrays.asList(new Solicitacao()));
        
        // Mock processoRepository calls - each called twice in implementation
        when(processoRepository.countByStatus("Ativo")).thenReturn(50L);
        when(processoRepository.countByStatus("Concluído")).thenReturn(30L);
        
        // Mock correspondenteRepository calls
        Usuario correspondente1 = new Usuario();
        correspondente1.setTipo(3);
        correspondente1.setAtivo(true);
        
        Usuario correspondente2 = new Usuario();
        correspondente2.setTipo(3);
        correspondente2.setAtivo(false);
        
        when(usuarioRepository.findByTipo(3)).thenReturn(Arrays.asList(correspondente1, correspondente2));
        
        // Mock status statistics
        List<Object[]> statusResults = Arrays.asList(
            new Object[]{"Pendente", 40L},
            new Object[]{"Concluído", 60L}
        );
        when(solicitacaoRepository.countByStatusSolicitacao()).thenReturn(statusResults);
        
        // Mock comarca statistics
        List<Object[]> comarcaResults = Arrays.asList(
            new Object[]{1L, "Comarca 1", 25L},
            new Object[]{2L, "Comarca 2", 15L}
        );
        when(solicitacaoRepository.countByComarca()).thenReturn(comarcaResults);
        
        // Mock tipo solicitacao statistics
        List<Object[]> tipoResults = Arrays.asList(
            new Object[]{1L, "Tipo 1", 30L},
            new Object[]{2L, "Tipo 2", 20L}
        );
        when(solicitacaoRepository.countByTipoSolicitacao()).thenReturn(tipoResults);
        
        // Mock recent solicitacoes
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(1L);
        solicitacao.setDatasolicitacao(LocalDateTime.now());
        
        org.springframework.data.domain.Page<Solicitacao> solicitacaoPage = 
            new org.springframework.data.domain.PageImpl<>(Arrays.asList(solicitacao));
        when(solicitacaoRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(solicitacaoPage);
        
        SolicitacaoDTO solicitacaoDTO = new SolicitacaoDTO();
        solicitacaoDTO.setIdsolicitacao(1L);
        when(solicitacaoService.toDTO(any(Solicitacao.class))).thenReturn(solicitacaoDTO);

        // When
        DashboardDTO result = dashboardService.getDashboardData();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getPendentes());
        assertEquals(100L, result.getTotalSolicitacoes());
        assertEquals(1L, result.getPagas());
        assertEquals(2L, result.getNaoPagas());
        assertEquals(1L, result.getAtrasadas());
        assertEquals(50L, result.getProcessosAtivos());
        assertEquals(30L, result.getProcessosConcluidos());
        assertEquals(2L, result.getTotalCorrespondentes());
        assertEquals(1L, result.getCorrespondentesAtivos());
        assertEquals(1L, result.getCorrespondentesInativos());
        assertNotNull(result.getSolicitacoesPorStatus());
        assertEquals(2, result.getSolicitacoesPorStatus().size());
        assertNotNull(result.getTopComarcas());
        assertEquals(2, result.getTopComarcas().size());
        assertNotNull(result.getSolicitacoesPorTipo());
        assertEquals(2, result.getSolicitacoesPorTipo().size());
        assertNotNull(result.getRecentSolicitacoes());
        assertEquals(1, result.getRecentSolicitacoes().size());
        
        // Verify interactions - Note: some methods are called multiple times in the implementation
        verify(solicitacaoRepository, times(2)).countPendentes(); // Called twice in implementation
        verify(solicitacaoRepository, times(2)).count(); // Called twice in implementation
        verify(solicitacaoRepository, times(1)).findByPagoTrue();
        verify(solicitacaoRepository, times(1)).findByPagoFalse();
        verify(solicitacaoRepository, times(1)).findAtrasadas(any(LocalDateTime.class));
        verify(processoRepository, times(2)).countByStatus("Ativo"); // Called twice in implementation
        verify(processoRepository, times(2)).countByStatus("Concluído"); // Called twice in implementation
        verify(usuarioRepository, times(1)).findByTipo(3);
        verify(solicitacaoRepository, times(1)).countByStatusSolicitacao();
        verify(solicitacaoRepository, times(1)).countByComarca();
        verify(solicitacaoRepository, times(1)).countByTipoSolicitacao();
        verify(solicitacaoRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
        verify(solicitacaoService, times(1)).toDTO(any(Solicitacao.class));
    }

    @Test
    void testGetDashboardDataForCorrespondente_ExistingCorrespondent_ShouldReturnDashboardData() {
        // Given
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(testCorrespondente));
        // Note: countByCorrespondente is called twice in the implementation
        when(solicitacaoRepository.countByCorrespondente(testCorrespondente)).thenReturn(50L);
        // Note: countPendentesByCorrespondente is called twice in the implementation
        when(solicitacaoRepository.countPendentesByCorrespondente(testCorrespondente)).thenReturn(20L);
        when(solicitacaoRepository.countPagasByCorrespondente(testCorrespondente)).thenReturn(30L);
        when(solicitacaoRepository.countNaoPagasByCorrespondente(testCorrespondente)).thenReturn(20L);
        when(solicitacaoRepository.countAtrasadasByCorrespondente(eq(testCorrespondente), any(LocalDateTime.class))).thenReturn(5L);
        
        List<Object[]> statusResults = Arrays.asList(
            new Object[]{"Pendente", 20L},
            new Object[]{"Concluído", 30L}
        );
        when(solicitacaoRepository.countByStatusSolicitacaoAndCorrespondente(testCorrespondente)).thenReturn(statusResults);
        
        List<Object[]> comarcaResults = Arrays.asList(
            new Object[]{1L, "Comarca 1", 25L},
            new Object[]{2L, "Comarca 2", 15L}
        );
        when(solicitacaoRepository.countByComarcaAndCorrespondente(testCorrespondente)).thenReturn(comarcaResults);
        
        List<Object[]> tipoResults = Arrays.asList(
            new Object[]{1L, "Tipo 1", 30L},
            new Object[]{2L, "Tipo 2", 20L}
        );
        when(solicitacaoRepository.countByTipoSolicitacaoAndCorrespondente(testCorrespondente)).thenReturn(tipoResults);
        
        // Mock recent solicitacoes for correspondent
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(1L);
        solicitacao.setDatasolicitacao(LocalDateTime.now());
        
        org.springframework.data.domain.Page<Solicitacao> solicitacaoPage = 
            new org.springframework.data.domain.PageImpl<>(Arrays.asList(solicitacao));
        when(solicitacaoRepository.findByCorrespondente(eq(testCorrespondente), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(solicitacaoPage);
        
        SolicitacaoDTO solicitacaoDTO = new SolicitacaoDTO();
        solicitacaoDTO.setIdsolicitacao(1L);
        when(solicitacaoService.toDTO(any(Solicitacao.class))).thenReturn(solicitacaoDTO);

        // When
        DashboardDTO result = dashboardService.getDashboardDataForCorrespondente(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCorrespondenteId());
        assertEquals(50L, result.getTotalSolicitacoes());
        assertEquals(20L, result.getPendentes());
        assertEquals(30L, result.getConcluidas());
        assertEquals(30L, result.getPagas());
        assertEquals(20L, result.getNaoPagas());
        assertEquals(5L, result.getAtrasadas());
        assertNotNull(result.getSolicitacoesPorStatus());
        assertEquals(2, result.getSolicitacoesPorStatus().size());
        assertNotNull(result.getTopComarcas());
        assertEquals(2, result.getTopComarcas().size());
        assertNotNull(result.getSolicitacoesPorTipo());
        assertEquals(2, result.getSolicitacoesPorTipo().size());
        assertNotNull(result.getRecentSolicitacoes());
        assertEquals(1, result.getRecentSolicitacoes().size());
        
        // Verify interactions - Note: some methods are called multiple times in the implementation
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(solicitacaoRepository, times(2)).countByCorrespondente(testCorrespondente); // Called twice in implementation
        verify(solicitacaoRepository, times(2)).countPendentesByCorrespondente(testCorrespondente); // Called twice in implementation
        verify(solicitacaoRepository, times(1)).countPagasByCorrespondente(testCorrespondente);
        verify(solicitacaoRepository, times(1)).countNaoPagasByCorrespondente(testCorrespondente);
        verify(solicitacaoRepository, times(1)).countAtrasadasByCorrespondente(eq(testCorrespondente), any(LocalDateTime.class));
        verify(solicitacaoRepository, times(1)).countByStatusSolicitacaoAndCorrespondente(testCorrespondente);
        verify(solicitacaoRepository, times(1)).countByComarcaAndCorrespondente(testCorrespondente);
        verify(solicitacaoRepository, times(1)).countByTipoSolicitacaoAndCorrespondente(testCorrespondente);
        verify(solicitacaoRepository, times(1)).findByCorrespondente(eq(testCorrespondente), any(org.springframework.data.domain.Pageable.class));
        verify(solicitacaoService, times(1)).toDTO(any(Solicitacao.class));
    }

    @Test
    void testGetDashboardDataForCorrespondente_NonExistentCorrespondent_ShouldThrowException() {
        // Given
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardService.getDashboardDataForCorrespondente(999L);
        });
        
        assertEquals("Correspondente not found with ID: 999", exception.getMessage());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(999L);
        verify(solicitacaoRepository, never()).countByCorrespondente(any());
    }
}