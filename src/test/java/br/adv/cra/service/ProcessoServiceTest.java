package br.adv.cra.service;

import br.adv.cra.dto.ProcessoDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import br.adv.cra.repository.ProcessoRepository;
import br.adv.cra.repository.OrgaoRepository;
import br.adv.cra.repository.ComarcaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProcessoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private OrgaoRepository orgaoRepository;

    @Mock
    private ComarcaRepository comarcaRepository;

    private ProcessoService processoService;

    private Processo testProcesso;
    private Orgao testOrgao;
    private Comarca testComarca;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processoService = new ProcessoService(processoRepository, orgaoRepository, comarcaRepository);

        // Create test entities
        testOrgao = new Orgao();
        testOrgao.setId(1L);
        testOrgao.setDescricao("Tribunal de Justiça");

        testComarca = new Comarca();
        testComarca.setId(1L);
        testComarca.setNome("Comarca de São Paulo");

        testProcesso = new Processo();
        testProcesso.setId(1L);
        testProcesso.setNumeroprocesso("1234567-89.2023.8.26.0001");
        testProcesso.setNumeroprocessopesq("12345678920238260001");
        testProcesso.setParte("João Silva");
        testProcesso.setAdverso("Maria Santos");
        testProcesso.setStatus("Ativo");
        testProcesso.setOrgao(testOrgao);
        testProcesso.setComarca(testComarca);
        testProcesso.setDatadistribuicao(new Date());
    }

    @Test
    void testSalvar_ProcessoWithValidOrgaoAndComarca_ShouldSaveProcesso() {
        // Given
        Processo processoToSave = new Processo();
        processoToSave.setNumeroprocesso("9876543-21.2023.8.26.0001");
        
        // Set orgao and comarca objects
        Orgao orgao = new Orgao();
        orgao.setId(1L);
        processoToSave.setOrgao(orgao);
        
        Comarca comarca = new Comarca();
        comarca.setId(1L);
        processoToSave.setComarca(comarca);

        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(testOrgao));
        when(comarcaRepository.findById(1L)).thenReturn(Optional.of(testComarca));
        when(processoRepository.save(any(Processo.class))).thenReturn(testProcesso);

        // When
        Processo result = processoService.salvar(processoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testProcesso, result);
        assertEquals(testOrgao, result.getOrgao());
        assertEquals(testComarca, result.getComarca());

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(1L);
        verify(comarcaRepository, times(1)).findById(1L);
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void testSalvar_ProcessoWithInvalidOrgao_ShouldThrowException() {
        // Given
        Processo processoToSave = new Processo();
        processoToSave.setNumeroprocesso("9876543-21.2023.8.26.0001");
        
        // Set orgao object with invalid ID
        Orgao orgao = new Orgao();
        orgao.setId(999L);
        processoToSave.setOrgao(orgao);

        when(orgaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processoService.salvar(processoToSave);
        });

        assertEquals("Órgão com ID 999 não encontrado", exception.getMessage());

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(999L);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void testSalvar_ProcessoWithInvalidComarca_ShouldThrowException() {
        // Given
        Processo processoToSave = new Processo();
        processoToSave.setNumeroprocesso("9876543-21.2023.8.26.0001");
        
        // Set comarca object with invalid ID
        Comarca comarca = new Comarca();
        comarca.setId(999L);
        processoToSave.setComarca(comarca);

        when(comarcaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processoService.salvar(processoToSave);
        });

        assertEquals("Comarca com ID 999 não encontrada", exception.getMessage());

        // Verify interactions
        verify(comarcaRepository, times(1)).findById(999L);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void testSalvarComDTO_ValidProcessoDTO_ShouldSaveProcesso() {
        // Given
        ProcessoDTO processoDTO = new ProcessoDTO();
        processoDTO.setNumeroprocesso("1234567-89.2023.8.26.0001");
        processoDTO.setOrgaoId(1L);
        processoDTO.setComarcaId(1L);

        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(testOrgao));
        when(comarcaRepository.findById(1L)).thenReturn(Optional.of(testComarca));
        when(processoRepository.save(any(Processo.class))).thenReturn(testProcesso);

        // When
        Processo result = processoService.salvarComDTO(processoDTO);

        // Then
        assertNotNull(result);
        assertEquals(testProcesso, result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(1L);
        verify(comarcaRepository, times(1)).findById(1L);
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void testSalvarComDTO_InvalidOrgaoId_ShouldThrowException() {
        // Given
        ProcessoDTO processoDTO = new ProcessoDTO();
        processoDTO.setNumeroprocesso("1234567-89.2023.8.26.0001");
        processoDTO.setOrgaoId(999L);

        when(orgaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processoService.salvarComDTO(processoDTO);
        });

        assertEquals("Órgão com ID 999 não encontrado", exception.getMessage());

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(999L);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void testAtualizar_ExistingProcesso_ShouldUpdateProcesso() {
        // Given
        Processo processoToUpdate = new Processo();
        processoToUpdate.setId(1L);
        processoToUpdate.setNumeroprocesso("1234567-89.2023.8.26.0001");
        
        // Set orgao and comarca objects
        Orgao orgao = new Orgao();
        orgao.setId(1L);
        processoToUpdate.setOrgao(orgao);
        
        Comarca comarca = new Comarca();
        comarca.setId(1L);
        processoToUpdate.setComarca(comarca);

        when(processoRepository.existsById(1L)).thenReturn(true);
        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(testOrgao));
        when(comarcaRepository.findById(1L)).thenReturn(Optional.of(testComarca));
        when(processoRepository.save(any(Processo.class))).thenReturn(processoToUpdate);

        // When
        Processo result = processoService.atualizar(processoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals(processoToUpdate, result);

        // Verify interactions
        verify(processoRepository, times(1)).existsById(1L);
        verify(orgaoRepository, times(1)).findById(1L);
        verify(comarcaRepository, times(1)).findById(1L);
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void testAtualizar_NonExistentProcesso_ShouldThrowException() {
        // Given
        Processo processoToUpdate = new Processo();
        processoToUpdate.setId(999L);

        when(processoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processoService.atualizar(processoToUpdate);
        });

        assertEquals("Processo não encontrado", exception.getMessage());

        // Verify interactions
        verify(processoRepository, times(1)).existsById(999L);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void testDeletar_ExistingProcesso_ShouldDeleteProcesso() {
        // Given
        when(processoRepository.existsById(1L)).thenReturn(true);

        // When
        processoService.deletar(1L);

        // Then
        // Verify interactions
        verify(processoRepository, times(1)).existsById(1L);
        verify(processoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentProcesso_ShouldThrowException() {
        // Given
        when(processoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processoService.deletar(999L);
        });

        assertEquals("Processo não encontrado", exception.getMessage());

        // Verify interactions
        verify(processoRepository, times(1)).existsById(999L);
        verify(processoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingProcesso_ShouldReturnProcesso() {
        // Given
        when(processoRepository.findById(1L)).thenReturn(Optional.of(testProcesso));

        // When
        Optional<Processo> result = processoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProcesso, result.get());

        // Verify interactions
        verify(processoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentProcesso_ShouldReturnEmpty() {
        // Given
        when(processoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Processo> result = processoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(processoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllProcessosAsDTOs() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("1234567-89.2023.8.26.0001");
        processo1.setOrgao(testOrgao);
        processo1.setComarca(testComarca);

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("9876543-21.2023.8.26.0001");
        processo2.setOrgao(testOrgao);
        processo2.setComarca(testComarca);

        when(processoRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<ProcessoDTO> result = processoService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1234567-89.2023.8.26.0001", result.get(0).getNumeroprocesso());
        assertEquals("9876543-21.2023.8.26.0001", result.get(1).getNumeroprocesso());

        // Verify interactions
        verify(processoRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarTodos_ShouldReturnAllProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("1234567-89.2023.8.26.0001");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("9876543-21.2023.8.26.0001");

        when(processoRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1234567-89.2023.8.26.0001", result.get(0).getNumeroprocesso());
        assertEquals("9876543-21.2023.8.26.0001", result.get(1).getNumeroprocesso());

        // Verify interactions
        verify(processoRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarTodosWithSort_ShouldReturnAllProcessosWithCustomSorting() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("AAA");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("BBB");

        Sort sort = Sort.by(Sort.Direction.DESC, "numeroprocesso");

        when(processoRepository.findAll(sort)).thenReturn(Arrays.asList(processo2, processo1));

        // When
        List<Processo> result = processoService.listarTodos(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BBB", result.get(0).getNumeroprocesso());
        assertEquals("AAA", result.get(1).getNumeroprocesso());

        // Verify interactions
        verify(processoRepository, times(1)).findAll(sort);
    }

    @Test
    void testListarTodosWithPageable_ShouldReturnPageOfProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("1234567-89.2023.8.26.0001");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("9876543-21.2023.8.26.0001");

        Page<Processo> processoPage = new PageImpl<>(Arrays.asList(processo1, processo2));
        Pageable pageable = PageRequest.of(0, 10);

        when(processoRepository.findAll(pageable)).thenReturn(processoPage);

        // When
        Page<Processo> result = processoService.listarTodos(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("1234567-89.2023.8.26.0001", result.getContent().get(0).getNumeroprocesso());
        assertEquals("9876543-21.2023.8.26.0001", result.getContent().get(1).getNumeroprocesso());

        // Verify interactions
        verify(processoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListarTodosDTOWithPageable_ShouldReturnPageOfProcessosAsDTOs() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("1234567-89.2023.8.26.0001");
        processo1.setOrgao(testOrgao);
        processo1.setComarca(testComarca);

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("9876543-21.2023.8.26.0001");
        processo2.setOrgao(testOrgao);
        processo2.setComarca(testComarca);

        Page<Processo> processoPage = new PageImpl<>(Arrays.asList(processo1, processo2));
        Pageable pageable = PageRequest.of(0, 10);

        when(processoRepository.findAll(pageable)).thenReturn(processoPage);

        // When
        Page<ProcessoDTO> result = processoService.listarTodosDTO(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("1234567-89.2023.8.26.0001", result.getContent().get(0).getNumeroprocesso());
        assertEquals("9876543-21.2023.8.26.0001", result.getContent().get(1).getNumeroprocesso());

        // Verify interactions
        verify(processoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testContarTodos_ShouldReturnTotalCount() {
        // Given
        when(processoRepository.countAllProcessos()).thenReturn(5L);

        // When
        long result = processoService.contarTodos();

        // Then
        assertEquals(5L, result);

        // Verify interactions
        verify(processoRepository, times(1)).countAllProcessos();
    }

    @Test
    void testBuscarPorNumeroProcesso_ExistingProcesso_ShouldReturnProcesso() {
        // Given
        when(processoRepository.findByNumeroprocesso("1234567-89.2023.8.26.0001")).thenReturn(Optional.of(testProcesso));

        // When
        Optional<Processo> result = processoService.buscarPorNumeroProcesso("1234567-89.2023.8.26.0001");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProcesso, result.get());

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocesso("1234567-89.2023.8.26.0001");
    }

    @Test
    void testBuscarPorNumeroProcesso_NonExistentProcesso_ShouldReturnEmpty() {
        // Given
        when(processoRepository.findByNumeroprocesso("9999999-99.9999.8.26.9999")).thenReturn(Optional.empty());

        // When
        Optional<Processo> result = processoService.buscarPorNumeroProcesso("9999999-99.9999.8.26.9999");

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocesso("9999999-99.9999.8.26.9999");
    }

    @Test
    void testBuscarPorNumeroProcessoPesquisa_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocessopesq("12345678920238260001");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocessopesq("12345678920238260002");

        when(processoRepository.findByNumeroprocessopesqContaining(eq("123456"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorNumeroProcessoPesquisa("123456");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getNumeroprocessopesq().contains("123456"));
        assertTrue(result.get(1).getNumeroprocessopesq().contains("123456"));

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocessopesqContaining(eq("123456"), any(Sort.class));
    }

    @Test
    void testBuscarPorParte_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setParte("João Silva");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setParte("João Santos");

        when(processoRepository.findByParteContaining(eq("João"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorParte("João");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getParte().contains("João"));
        assertTrue(result.get(1).getParte().contains("João"));

        // Verify interactions
        verify(processoRepository, times(1)).findByParteContaining(eq("João"), any(Sort.class));
    }

    @Test
    void testBuscarPorAdverso_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setAdverso("Maria Santos");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setAdverso("Maria Oliveira");

        when(processoRepository.findByAdversoContaining(eq("Maria"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorAdverso("Maria");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getAdverso().contains("Maria"));
        assertTrue(result.get(1).getAdverso().contains("Maria"));

        // Verify interactions
        verify(processoRepository, times(1)).findByAdversoContaining(eq("Maria"), any(Sort.class));
    }

    @Test
    void testBuscarPorStatus_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setStatus("Ativo");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setStatus("Ativo");

        when(processoRepository.findByStatus(eq("Ativo"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorStatus("Ativo");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ativo", result.get(0).getStatus());
        assertEquals("Ativo", result.get(1).getStatus());

        // Verify interactions
        verify(processoRepository, times(1)).findByStatus(eq("Ativo"), any(Sort.class));
    }

    @Test
    void testBuscarPorComarca_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setComarca(testComarca);

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setComarca(testComarca);

        when(processoRepository.findByComarca(eq(testComarca), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorComarca(testComarca);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testComarca, result.get(0).getComarca());
        assertEquals(testComarca, result.get(1).getComarca());

        // Verify interactions
        verify(processoRepository, times(1)).findByComarca(eq(testComarca), any(Sort.class));
    }

    @Test
    void testBuscarPorOrgao_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setOrgao(testOrgao);

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setOrgao(testOrgao);

        when(processoRepository.findByOrgao(eq(testOrgao), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorOrgao(testOrgao);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testOrgao, result.get(0).getOrgao());
        assertEquals(testOrgao, result.get(1).getOrgao());

        // Verify interactions
        verify(processoRepository, times(1)).findByOrgao(eq(testOrgao), any(Sort.class));
    }

    @Test
    void testBuscarPorAssunto_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setAssunto("Danos Morais");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setAssunto("Danos Materiais");

        when(processoRepository.findByAssuntoContaining(eq("Danos"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorAssunto("Danos");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getAssunto().contains("Danos"));
        assertTrue(result.get(1).getAssunto().contains("Danos"));

        // Verify interactions
        verify(processoRepository, times(1)).findByAssuntoContaining(eq("Danos"), any(Sort.class));
    }

    @Test
    void testBuscarPorProcessoEletronico_ShouldReturnMatchingProcessos() {
        // Given
        Processo processo1 = new Processo();
        processo1.setId(1L);
        processo1.setProceletronico("PJE12345");

        Processo processo2 = new Processo();
        processo2.setId(2L);
        processo2.setProceletronico("PJE67890");

        when(processoRepository.findByProceletronico(eq("PJE"), any(Sort.class)))
            .thenReturn(Arrays.asList(processo1, processo2));

        // When
        List<Processo> result = processoService.buscarPorProcessoEletronico("PJE");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getProceletronico().contains("PJE"));
        assertTrue(result.get(1).getProceletronico().contains("PJE"));

        // Verify interactions
        verify(processoRepository, times(1)).findByProceletronico(eq("PJE"), any(Sort.class));
    }

    @Test
    void testExisteNumeroProcesso_ExistingProcesso_ShouldReturnTrue() {
        // Given
        when(processoRepository.existsByNumeroprocesso("1234567-89.2023.8.26.0001")).thenReturn(true);

        // When
        boolean result = processoService.existeNumeroProcesso("1234567-89.2023.8.26.0001");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(processoRepository, times(1)).existsByNumeroprocesso("1234567-89.2023.8.26.0001");
    }

    @Test
    void testExisteNumeroProcesso_NonExistentProcesso_ShouldReturnFalse() {
        // Given
        when(processoRepository.existsByNumeroprocesso("9999999-99.9999.8.26.9999")).thenReturn(false);

        // When
        boolean result = processoService.existeNumeroProcesso("9999999-99.9999.8.26.9999");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(processoRepository, times(1)).existsByNumeroprocesso("9999999-99.9999.8.26.9999");
    }

    @Test
    void testExisteNumeroProcessoParaOutroProcesso_ExistingProcessoForDifferentId_ShouldReturnTrue() {
        // Given
        Processo processo = new Processo();
        processo.setId(2L); // Different ID
        processo.setNumeroprocesso("1234567-89.2023.8.26.0001");

        when(processoRepository.findByNumeroprocesso("1234567-89.2023.8.26.0001")).thenReturn(Optional.of(processo));

        // When
        boolean result = processoService.existeNumeroProcessoParaOutroProcesso("1234567-89.2023.8.26.0001", 1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocesso("1234567-89.2023.8.26.0001");
    }

    @Test
    void testExisteNumeroProcessoParaOutroProcesso_ExistingProcessoForSameId_ShouldReturnFalse() {
        // Given
        Processo processo = new Processo();
        processo.setId(1L); // Same ID
        processo.setNumeroprocesso("1234567-89.2023.8.26.0001");

        when(processoRepository.findByNumeroprocesso("1234567-89.2023.8.26.0001")).thenReturn(Optional.of(processo));

        // When
        boolean result = processoService.existeNumeroProcessoParaOutroProcesso("1234567-89.2023.8.26.0001", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocesso("1234567-89.2023.8.26.0001");
    }

    @Test
    void testExisteNumeroProcessoParaOutroProcesso_NonExistentProcesso_ShouldReturnFalse() {
        // Given
        when(processoRepository.findByNumeroprocesso("9999999-99.9999.8.26.9999")).thenReturn(Optional.empty());

        // When
        boolean result = processoService.existeNumeroProcessoParaOutroProcesso("9999999-99.9999.8.26.9999", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(processoRepository, times(1)).findByNumeroprocesso("9999999-99.9999.8.26.9999");
    }

    @Test
    void testContarPorStatus_ShouldReturnCount() {
        // Given
        when(processoRepository.countByStatus("Ativo")).thenReturn(5L);

        // When
        Long result = processoService.contarPorStatus("Ativo");

        // Then
        assertEquals(5L, result);

        // Verify interactions
        verify(processoRepository, times(1)).countByStatus("Ativo");
    }
}