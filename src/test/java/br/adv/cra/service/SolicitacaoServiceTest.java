package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoDTO;
import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.*;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import br.adv.cra.specification.SolicitacaoSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private StatusSolicitacaoRepository statusSolicitacaoRepository;

    @Mock
    private SoliArquivoRepository soliArquivoRepository;

    private SolicitacaoService solicitacaoService;

    private Solicitacao testSolicitacao;
    private StatusSolicitacao testStatus;
    private Usuario testUsuario;
    private Processo testProcesso;
    private Comarca testComarca;
    private TipoSolicitacao testTipoSolicitacao;
    private Correspondente testCorrespondente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        solicitacaoService = new SolicitacaoService(solicitacaoRepository, statusSolicitacaoRepository, soliArquivoRepository);

        // Create test entities
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setNomecompleto("Test User");

        testComarca = new Comarca();
        testComarca.setId(1L);
        testComarca.setNome("Test Comarca");

        testProcesso = new Processo();
        testProcesso.setId(1L);
        testProcesso.setNumeroprocesso("1234567-89.2023.8.26.0001");

        testStatus = new StatusSolicitacao();
        testStatus.setIdstatus(1L);
        testStatus.setStatus("Pendente");

        testTipoSolicitacao = new TipoSolicitacao();
        testTipoSolicitacao.setIdtiposolicitacao(1L);
        testTipoSolicitacao.setEspecie("Certidão");

        testCorrespondente = new Correspondente();
        testCorrespondente.setId(1L);
        testCorrespondente.setNome("Test Correspondente");

        testSolicitacao = new Solicitacao();
        testSolicitacao.setId(1L);
        testSolicitacao.setNumero("SOL-001");
        testSolicitacao.setDatasolicitacao(LocalDateTime.now());
        testSolicitacao.setUsuario(testUsuario);
        testSolicitacao.setProcesso(testProcesso);
        testSolicitacao.setComarca(testComarca);
        testSolicitacao.setStatusSolicitacao(testStatus);
        testSolicitacao.setTipoSolicitacao(testTipoSolicitacao);
        testSolicitacao.setCorrespondente(testCorrespondente);
    }

    @Test
    void testSalvar_NewSolicitacao_ShouldSaveWithCurrentDate() {
        // Given
        Solicitacao solicitacaoToSave = new Solicitacao();
        solicitacaoToSave.setNumero("SOL-002");

        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.salvar(solicitacaoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testSolicitacao, result);
        assertNotNull(result.getDatasolicitacao());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).save(solicitacaoToSave);
    }

    @Test
    void testSalvar_NewSolicitacaoWithNullDate_ShouldSetCurrentDate() {
        // Given
        Solicitacao solicitacaoToSave = new Solicitacao();
        solicitacaoToSave.setNumero("SOL-002");
        solicitacaoToSave.setDatasolicitacao(null); // Explicitly set to null

        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.salvar(solicitacaoToSave);

        // Then
        assertNotNull(result);
        assertNotNull(result.getDatasolicitacao());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).save(solicitacaoToSave);
    }

    @Test
    void testAtualizar_ExistingSolicitacao_ShouldUpdateSolicitacao() {
        // Given
        Solicitacao solicitacaoToUpdate = new Solicitacao();
        solicitacaoToUpdate.setId(1L);
        solicitacaoToUpdate.setNumero("SOL-001-UPDATED");

        when(solicitacaoRepository.existsById(1L)).thenReturn(true);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoToUpdate);

        // When
        Solicitacao result = solicitacaoService.atualizar(solicitacaoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals(solicitacaoToUpdate, result);

        // Verify interactions
        verify(solicitacaoRepository, times(1)).existsById(1L);
        verify(solicitacaoRepository, times(1)).save(solicitacaoToUpdate);
    }

    @Test
    void testAtualizar_NonExistentSolicitacao_ShouldThrowException() {
        // Given
        Solicitacao solicitacaoToUpdate = new Solicitacao();
        solicitacaoToUpdate.setId(999L);

        when(solicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.atualizar(solicitacaoToUpdate);
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).existsById(999L);
        verify(solicitacaoRepository, never()).save(any(Solicitacao.class));
    }

    @Test
    void testSetStatus_ExistingSolicitacaoAndStatus_ShouldUpdateStatus() {
        // Given
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(statusSolicitacaoRepository.findById(2L)).thenReturn(Optional.of(new StatusSolicitacao()));
        when(solicitacaoRepository.saveAndFlush(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.setStatus(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(testSolicitacao, result);

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(statusSolicitacaoRepository, times(1)).findById(2L);
        verify(solicitacaoRepository, times(1)).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testSetStatus_NonExistentSolicitacao_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatus(999L, 1L);
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(999L);
        verify(statusSolicitacaoRepository, never()).findById(anyLong());
        verify(solicitacaoRepository, never()).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testSetStatus_NonExistentStatus_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(statusSolicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatus(1L, 999L);
        });

        assertEquals("Status não encontrado", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(statusSolicitacaoRepository, times(1)).findById(999L);
        verify(solicitacaoRepository, never()).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testSetStatusPorNome_ExistingSolicitacaoAndStatusName_ShouldUpdateStatus() {
        // Given
        StatusSolicitacao status = new StatusSolicitacao();
        status.setIdstatus(1L);
        status.setStatus("Concluída");

        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(statusSolicitacaoRepository.findByStatus("Concluída")).thenReturn(Optional.of(status));
        when(solicitacaoRepository.saveAndFlush(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.setStatusPorNome(1L, "Concluída");

        // Then
        assertNotNull(result);
        assertEquals(testSolicitacao, result);

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(statusSolicitacaoRepository, times(1)).findByStatus("Concluída");
        verify(solicitacaoRepository, times(1)).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testSetStatusPorNome_NonExistentSolicitacao_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatusPorNome(999L, "Concluída");
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(999L);
        verify(statusSolicitacaoRepository, never()).findByStatus(anyString());
        verify(solicitacaoRepository, never()).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testSetStatusPorNome_NonExistentStatusName_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(statusSolicitacaoRepository.findByStatus("NonExistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatusPorNome(1L, "NonExistent");
        });

        assertEquals("Status não encontrado", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(statusSolicitacaoRepository, times(1)).findByStatus("NonExistent");
        verify(solicitacaoRepository, never()).saveAndFlush(any(Solicitacao.class));
    }

    @Test
    void testConcluir_ExistingSolicitacao_ShouldSetConclusionDate() {
        // Given
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.concluir(1L, "Test conclusion");

        // Then
        assertNotNull(result);
        assertEquals(testSolicitacao, result);
        assertNotNull(result.getDataconclusao());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(solicitacaoRepository, times(1)).save(any(Solicitacao.class));
    }

    @Test
    void testConcluir_NonExistentSolicitacao_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.concluir(999L, "Test conclusion");
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(999L);
        verify(solicitacaoRepository, never()).save(any(Solicitacao.class));
    }

    @Test
    void testConcluir_WithObservation_ShouldAppendObservation() {
        // Given
        testSolicitacao.setObservacao("Existing observation");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(testSolicitacao);

        // When
        Solicitacao result = solicitacaoService.concluir(1L, "Test conclusion");

        // Then
        assertNotNull(result);
        assertTrue(result.getObservacao().contains("Existing observation"));
        assertTrue(result.getObservacao().contains("Conclusão: Test conclusion"));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(solicitacaoRepository, times(1)).save(any(Solicitacao.class));
    }

    @Test
    void testDeletar_ExistingSolicitacao_ShouldDeleteSolicitacao() {
        // Given
        when(solicitacaoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findBySolicitacaoIdsolicitacao(1L)).thenReturn(new ArrayList<>());

        // When
        solicitacaoService.deletar(1L);

        // Then
        // Verify interactions
        verify(solicitacaoRepository, times(1)).existsById(1L);
        verify(soliArquivoRepository, times(1)).findBySolicitacaoIdsolicitacao(1L);
        verify(soliArquivoRepository, times(1)).deleteAll(anyList());
        verify(solicitacaoRepository, times(1)).deleteHistoricoBySolicitacaoId(1L);
        verify(solicitacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentSolicitacao_ShouldThrowException() {
        // Given
        when(solicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.deletar(999L);
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).existsById(999L);
        verify(soliArquivoRepository, never()).findBySolicitacaoIdsolicitacao(anyLong());
        verify(soliArquivoRepository, never()).deleteAll(anyList());
        verify(solicitacaoRepository, never()).deleteHistoricoBySolicitacaoId(anyLong());
        verify(solicitacaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingSolicitacao_ShouldReturnSolicitacao() {
        // Given
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));

        // When
        Optional<Solicitacao> result = solicitacaoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSolicitacao, result.get());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentSolicitacao_ShouldReturnEmpty() {
        // Given
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Solicitacao> result = solicitacaoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodasDTO_ShouldReturnAllSolicitacoesAsDTOs() {
        // Given
        when(solicitacaoRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<SolicitacaoDTO> result = solicitacaoService.listarTodasDTO();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testToDTO_ShouldConvertSolicitacaoToDTO() {
        // When
        SolicitacaoDTO result = solicitacaoService.toDTO(testSolicitacao);

        // Then
        assertNotNull(result);
        assertEquals(testSolicitacao.getId(), result.getIdsolicitacao());
        assertEquals(testSolicitacao.getNumero(), result.getNumero());
        assertEquals(testSolicitacao.getDatasolicitacao(), result.getDatasolicitacao());
        assertEquals(testSolicitacao.getDataconclusao(), result.getDataconclusao());
        assertEquals(testComarca.getId(), result.getComarcaId());
        assertEquals(testComarca.getNome(), result.getComarcaNome());
        assertEquals(testProcesso.getId(), result.getProcessoId());
        assertEquals(testProcesso.getNumeroprocesso(), result.getProcessoNumero());
        assertEquals(testStatus.getIdstatus(), result.getStatusSolicitacaoId());
        assertEquals(testStatus.getStatus(), result.getStatusSolicitacaoStatus());
        assertEquals(testUsuario.getId(), result.getUsuarioId());
        assertEquals(testUsuario.getNomecompleto(), result.getUsuarioNome());
        assertEquals(testTipoSolicitacao.getIdtiposolicitacao(), result.getTipoSolicitacaoId());
        assertEquals(testTipoSolicitacao.getEspecie(), result.getTipoSolicitacaoEspecie());
        assertEquals(testCorrespondente.getId(), result.getCorrespondenteId());
        assertEquals(testCorrespondente.getNome(), result.getCorrespondenteNome());
    }

    @Test
    void testListarTodas_ShouldReturnAllSolicitacoes() {
        // Given
        when(solicitacaoRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.listarTodas();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarTodasWithSort_ShouldReturnAllSolicitacoesWithCustomSorting() {
        // Given
        Sort sort = Sort.by(Sort.Direction.DESC, "datasolicitacao");
        when(solicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.listarTodas(sort);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findAll(sort);
    }

    @Test
    void testListarTodasWithPageable_ShouldReturnPageOfSolicitacoes() {
        // Given
        Page<Solicitacao> solicitacaoPage = new PageImpl<>(Arrays.asList(testSolicitacao));
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findAll(pageable)).thenReturn(solicitacaoPage);

        // When
        Page<Solicitacao> result = solicitacaoService.listarTodas(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testSolicitacao, result.getContent().get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListarTodasDTOWithPageable_ShouldReturnPageOfSolicitacoesAsDTOs() {
        // Given
        Page<Solicitacao> solicitacaoPage = new PageImpl<>(Arrays.asList(testSolicitacao));
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findAll(pageable)).thenReturn(solicitacaoPage);

        // When
        Page<SolicitacaoDTO> result = solicitacaoService.listarTodasDTO(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testContarTodas_ShouldReturnTotalCount() {
        // Given
        when(solicitacaoRepository.countAllSolicitacoes()).thenReturn(5L);

        // When
        long result = solicitacaoService.contarTodas();

        // Then
        assertEquals(5L, result);

        // Verify interactions
        verify(solicitacaoRepository, times(1)).countAllSolicitacoes();
    }

    @Test
    void testBuscarPorUsuario_ShouldReturnSolicitacoesForUsuario() {
        // Given
        when(solicitacaoRepository.findByUsuario(eq(testUsuario), any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.buscarPorUsuario(testUsuario);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findByUsuario(eq(testUsuario), any(Sort.class));
    }

    @Test
    void testBuscarPorProcesso_ShouldReturnSolicitacoesForProcesso() {
        // Given
        when(solicitacaoRepository.findByProcesso(eq(testProcesso), any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.buscarPorProcesso(testProcesso);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findByProcesso(eq(testProcesso), any(Sort.class));
    }

    @Test
    void testBuscarPorComarca_ShouldReturnSolicitacoesForComarca() {
        // Given
        when(solicitacaoRepository.findByComarca(eq(testComarca), any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.buscarPorComarca(testComarca);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findByComarca(eq(testComarca), any(Sort.class));
    }

    @Test
    void testBuscarPorCorrespondente_ShouldReturnSolicitacoesForCorrespondente() {
        // Given
        when(solicitacaoRepository.findByCorrespondente(eq(testCorrespondente), any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(testCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findByCorrespondente(eq(testCorrespondente), any(Sort.class));
    }
    
    @Test
    void testBuscarPorCorrespondente_WithSort() {
        // Given
        when(solicitacaoRepository.findByCorrespondente(eq(testCorrespondente), any(Sort.class))).thenReturn(Arrays.asList(testSolicitacao));

        // When
        List<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(testCorrespondente, Sort.by(Sort.Direction.DESC, "datasolicitacao"));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSolicitacao, result.get(0));

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findByCorrespondente(eq(testCorrespondente), any(Sort.class));
    }
    
    @Test
    void testBuscarPorCorrespondente_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByCorrespondente(any(Correspondente.class), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(testCorrespondente, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByCorrespondente(any(Correspondente.class), any(Pageable.class));
    }
    
    // Test buscarPorUsuarioECorrespondente methods
    @Test
    void testBuscarPorUsuarioECorrespondente_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(testUsuario, testCorrespondente);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Sort.class));
    }
    
    @Test
    void testBuscarPorUsuarioECorrespondente_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(testUsuario, testCorrespondente, sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Sort.class));
    }
    
    @Test
    void testBuscarPorUsuarioECorrespondente_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(testUsuario, testCorrespondente, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByUsuarioAndCorrespondente(any(Usuario.class), any(Correspondente.class), any(Pageable.class));
    }
    
    // Test buscarPorPeriodo methods
    @Test
    void testBuscarPorPeriodo_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        when(solicitacaoRepository.findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorPeriodo(inicio, fim);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class));
    }
    
    @Test
    void testBuscarPorPeriodo_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorPeriodo(inicio, fim, sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class));
    }
    
    @Test
    void testBuscarPorPeriodo_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        when(solicitacaoRepository.findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorPeriodo(inicio, fim, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByDatasolicitacaoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }
    
    // Test listarPendentes methods
    @Test
    void testListarPendentes_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findPendentes(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarPendentes();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findPendentes(any(Sort.class));
    }
    
    @Test
    void testListarPendentes_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findPendentes(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarPendentes(sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findPendentes(any(Sort.class));
    }
    
    @Test
    void testListarPendentes_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findPendentes(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.listarPendentes(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findPendentes(any(Pageable.class));
    }
    
    // Test listarConcluidas methods
    @Test
    void testListarConcluidas_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findConcluidas(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarConcluidas();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findConcluidas(any(Sort.class));
    }
    
    @Test
    void testListarConcluidas_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "dataconclusao");
        when(solicitacaoRepository.findConcluidas(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarConcluidas(sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findConcluidas(any(Sort.class));
    }
    
    @Test
    void testListarConcluidas_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findConcluidas(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.listarConcluidas(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findConcluidas(any(Pageable.class));
    }
    
    // Test listarPagas methods
    @Test
    void testListarPagas_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByPagoTrue(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarPagas();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByPagoTrue(any(Sort.class));
    }
    
    @Test
    void testListarPagas_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByPagoTrue(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarPagas(sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByPagoTrue(any(Sort.class));
    }
    
    @Test
    void testListarPagas_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByPagoTrue(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.listarPagas(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByPagoTrue(any(Pageable.class));
    }
    
    // Test listarNaoPagas methods
    @Test
    void testListarNaoPagas_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByPagoFalse(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarNaoPagas();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByPagoFalse(any(Sort.class));
    }
    
    @Test
    void testListarNaoPagas_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByPagoFalse(any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarNaoPagas(sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByPagoFalse(any(Sort.class));
    }
    
    @Test
    void testListarNaoPagas_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByPagoFalse(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.listarNaoPagas(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByPagoFalse(any(Pageable.class));
    }
    
    // Test listarAtrasadas methods
    @Test
    void testListarAtrasadas_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        LocalDateTime now = LocalDateTime.now();
        when(solicitacaoRepository.findAtrasadas(any(LocalDateTime.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarAtrasadas();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findAtrasadas(any(LocalDateTime.class), any(Sort.class));
    }
    
    @Test
    void testListarAtrasadas_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.ASC, "dataprazo");
        when(solicitacaoRepository.findAtrasadas(any(LocalDateTime.class), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.listarAtrasadas(sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findAtrasadas(any(LocalDateTime.class), any(Sort.class));
    }
    
    @Test
    void testListarAtrasadas_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        when(solicitacaoRepository.findAtrasadas(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.listarAtrasadas(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findAtrasadas(any(LocalDateTime.class), any(Pageable.class));
    }
    
    // Test buscarPorTexto methods
    @Test
    void testBuscarPorTexto_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByTextoContaining(anyString(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorTexto("test");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByTextoContaining(anyString(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorTexto_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByTextoContaining(anyString(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorTexto("test", sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByTextoContaining(anyString(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorTexto_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByTextoContaining(anyString(), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorTexto("test", pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByTextoContaining(anyString(), any(Pageable.class));
    }
    
    // Test buscarPorGrupo methods
    @Test
    void testBuscarPorGrupo_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByGrupo(anyInt(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorGrupo(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByGrupo(anyInt(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorGrupo_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByGrupo(anyInt(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorGrupo(1, sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByGrupo(anyInt(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorGrupo_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByGrupo(anyInt(), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorGrupo(1, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByGrupo(anyInt(), any(Pageable.class));
    }
    
    // Test buscarPorStatusExterno methods
    @Test
    void testBuscarPorStatusExterno_NoPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        when(solicitacaoRepository.findByStatusexterno(anyString(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("CONFIRMAR");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByStatusexterno(anyString(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorStatusExterno_WithSort() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Sort sort = Sort.by(Sort.Direction.ASC, "datasolicitacao");
        when(solicitacaoRepository.findByStatusexterno(anyString(), any(Sort.class))).thenReturn(solicitacoes);
        
        // When
        List<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("CONFIRMAR", sort);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(solicitacaoRepository, times(1)).findByStatusexterno(anyString(), any(Sort.class));
    }
    
    @Test
    void testBuscarPorStatusExterno_WithPagination() {
        // Given
        List<Solicitacao> solicitacoes = Arrays.asList(testSolicitacao);
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        Pageable pageable = PageRequest.of(0, 10);
        when(solicitacaoRepository.findByStatusexterno(anyString(), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("CONFIRMAR", pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findByStatusexterno(anyString(), any(Pageable.class));
    }
    
    // Test contarPorUsuario method
    @Test
    void testContarPorUsuario() {
        // Given
        when(solicitacaoRepository.countByUsuario(any(Usuario.class))).thenReturn(5L);
        
        // When
        Long result = solicitacaoService.contarPorUsuario(testUsuario);
        
        // Then
        assertEquals(Long.valueOf(5), result);
        verify(solicitacaoRepository, times(1)).countByUsuario(any(Usuario.class));
    }
}