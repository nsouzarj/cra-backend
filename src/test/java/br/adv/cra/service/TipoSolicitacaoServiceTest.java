package br.adv.cra.service;

import br.adv.cra.dto.TipoSolicitacaoDTO;
import br.adv.cra.entity.TipoSolicitacao;
import br.adv.cra.repository.TipoSolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TipoSolicitacaoServiceTest {

    @Mock
    private TipoSolicitacaoRepository tipoSolicitacaoRepository;

    private TipoSolicitacaoService tipoSolicitacaoService;

    private TipoSolicitacao testTipo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tipoSolicitacaoService = new TipoSolicitacaoService(tipoSolicitacaoRepository);

        // Create test tipo solicitacao
        testTipo = new TipoSolicitacao();
        testTipo.setIdtiposolicitacao(1L);
        testTipo.setEspecie("Certidão");
        testTipo.setDescricao("Certidão de Nascimento");
        testTipo.setTipo("Documento");
        testTipo.setVisualizar(true);
    }

    @Test
    void testSalvar_ShouldSaveTipoSolicitacao() {
        // Given
        TipoSolicitacao tipoToSave = new TipoSolicitacao();
        tipoToSave.setEspecie("Certidão");
        tipoToSave.setDescricao("Certidão de Casamento");
        tipoToSave.setTipo("Documento");
        tipoToSave.setVisualizar(true);

        when(tipoSolicitacaoRepository.save(any(TipoSolicitacao.class))).thenReturn(testTipo);

        // When
        TipoSolicitacao result = tipoSolicitacaoService.salvar(tipoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testTipo, result);

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).save(tipoToSave);
    }

    @Test
    void testAtualizar_ExistingTipo_ShouldUpdateTipo() {
        // Given
        TipoSolicitacao tipoToUpdate = new TipoSolicitacao();
        tipoToUpdate.setIdtiposolicitacao(1L);
        tipoToUpdate.setEspecie("Certidão Atualizada");
        tipoToUpdate.setDescricao("Certidão de Nascimento Atualizada");
        tipoToUpdate.setTipo("Documento");
        tipoToUpdate.setVisualizar(false);

        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);
        when(tipoSolicitacaoRepository.save(any(TipoSolicitacao.class))).thenReturn(tipoToUpdate);

        // When
        TipoSolicitacao result = tipoSolicitacaoService.atualizar(tipoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Certidão Atualizada", result.getEspecie());
        assertFalse(result.getVisualizar());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(1L);
        verify(tipoSolicitacaoRepository, times(1)).save(tipoToUpdate);
    }

    @Test
    void testAtualizar_NonExistentTipo_ShouldThrowException() {
        // Given
        TipoSolicitacao tipoToUpdate = new TipoSolicitacao();
        tipoToUpdate.setIdtiposolicitacao(999L);
        tipoToUpdate.setEspecie("Non-existent");

        when(tipoSolicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tipoSolicitacaoService.atualizar(tipoToUpdate);
        });

        assertEquals("Tipo de Solicitação não encontrado", exception.getMessage());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(999L);
        verify(tipoSolicitacaoRepository, never()).save(any(TipoSolicitacao.class));
    }

    @Test
    void testDeletar_ExistingTipo_ShouldDeleteTipo() {
        // Given
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);

        // When
        tipoSolicitacaoService.deletar(1L);

        // Then
        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(1L);
        verify(tipoSolicitacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentTipo_ShouldThrowException() {
        // Given
        when(tipoSolicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tipoSolicitacaoService.deletar(999L);
        });

        assertEquals("Tipo de Solicitação não encontrado", exception.getMessage());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(999L);
        verify(tipoSolicitacaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingTipo_ShouldReturnTipo() {
        // Given
        when(tipoSolicitacaoRepository.findById(1L)).thenReturn(Optional.of(testTipo));

        // When
        Optional<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTipo, result.get());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentTipo_ShouldReturnEmpty() {
        // Given
        when(tipoSolicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllTiposAsDTOs() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findAll()).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacaoDTO> result = tipoSolicitacaoService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Certidão", result.get(0).getEspecie());
        assertEquals("Certidão de Nascimento", result.get(0).getDescricao());
        assertEquals("Documento", result.get(0).getTipo());
        assertTrue(result.get(0).getVisualizar());
        assertEquals("Autenticação", result.get(1).getEspecie());
        assertEquals("Autenticação de Documentos", result.get(1).getDescricao());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findAll();
    }

    @Test
    void testListarTodos_ShouldReturnAllTipos() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findAll()).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Certidão", result.get(0).getEspecie());
        assertEquals("Autenticação", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findAll();
    }

    @Test
    void testListarTodosWithSort_ShouldReturnAllTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "especie");

        when(tipoSolicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodos(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Autenticação", result.get(0).getEspecie());
        assertEquals("Certidão", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findAll(sort);
    }

    @Test
    void testListarTodosOrdenados_ShouldReturnAllTiposOrderedByEspecie() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findAllOrderByEspecie()).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodosOrdenados();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Certidão", result.get(0).getEspecie());
        assertEquals("Autenticação", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findAllOrderByEspecie();
    }

    @Test
    void testListarTodosOrdenadosWithSort_ShouldReturnAllTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "especie");

        when(tipoSolicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodosOrdenados(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Autenticação", result.get(0).getEspecie());
        assertEquals("Certidão", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findAll(sort);
    }

    @Test
    void testBuscarPorEspecie_ShouldReturnMatchingTipos() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão de Nascimento");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Certidão de Casamento");
        tipo2.setDescricao("Certidão de Casamento");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findByEspecieContaining("Certidão")).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorEspecie("Certidão");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getEspecie().contains("Certidão"));
        assertTrue(result.get(1).getEspecie().contains("Certidão"));

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByEspecieContaining("Certidão");
    }

    @Test
    void testBuscarPorEspecieWithSort_ShouldReturnMatchingTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão de Nascimento");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Certidão de Casamento");
        tipo2.setDescricao("Certidão de Casamento");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "especie");

        when(tipoSolicitacaoRepository.findByEspecieContaining(eq("Certidão"), any(Sort.class))).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorEspecie("Certidão", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Certidão de Casamento", result.get(0).getEspecie());
        assertEquals("Certidão de Nascimento", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByEspecieContaining(eq("Certidão"), any(Sort.class));
    }

    @Test
    void testBuscarPorDescricao_ShouldReturnMatchingTipos() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Certidão");
        tipo2.setDescricao("Certidão de Casamento");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findByDescricaoContaining("Certidão")).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorDescricao("Certidão");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDescricao().contains("Certidão"));
        assertTrue(result.get(1).getDescricao().contains("Certidão"));

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByDescricaoContaining("Certidão");
    }

    @Test
    void testBuscarPorDescricaoWithSort_ShouldReturnMatchingTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Certidão");
        tipo2.setDescricao("Certidão de Casamento");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "descricao");

        when(tipoSolicitacaoRepository.findByDescricaoContaining(eq("Certidão"), any(Sort.class))).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorDescricao("Certidão", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Certidão de Casamento", result.get(0).getDescricao());
        assertEquals("Certidão de Nascimento", result.get(1).getDescricao());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByDescricaoContaining(eq("Certidão"), any(Sort.class));
    }

    @Test
    void testBuscarPorTipo_ShouldReturnMatchingTipos() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findByTipo("Documento")).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorTipo("Documento");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Documento", result.get(0).getTipo());
        assertEquals("Documento", result.get(1).getTipo());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByTipo("Documento");
    }

    @Test
    void testBuscarPorTipoWithSort_ShouldReturnMatchingTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "especie");

        when(tipoSolicitacaoRepository.findByTipo(eq("Documento"), any(Sort.class))).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorTipo("Documento", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Autenticação", result.get(0).getEspecie());
        assertEquals("Certidão", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByTipo(eq("Documento"), any(Sort.class));
    }

    @Test
    void testBuscarPorVisualizar_ShouldReturnMatchingTipos() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        when(tipoSolicitacaoRepository.findByVisualizar(true)).thenReturn(Arrays.asList(tipo1, tipo2));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorVisualizar(true);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getVisualizar());
        assertTrue(result.get(1).getVisualizar());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByVisualizar(true);
    }

    @Test
    void testBuscarPorVisualizarWithSort_ShouldReturnMatchingTiposWithCustomSorting() {
        // Given
        TipoSolicitacao tipo1 = new TipoSolicitacao();
        tipo1.setIdtiposolicitacao(1L);
        tipo1.setEspecie("Certidão");
        tipo1.setDescricao("Certidão de Nascimento");
        tipo1.setTipo("Documento");
        tipo1.setVisualizar(true);

        TipoSolicitacao tipo2 = new TipoSolicitacao();
        tipo2.setIdtiposolicitacao(2L);
        tipo2.setEspecie("Autenticação");
        tipo2.setDescricao("Autenticação de Documentos");
        tipo2.setTipo("Documento");
        tipo2.setVisualizar(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "especie");

        when(tipoSolicitacaoRepository.findByVisualizar(eq(true), any(Sort.class))).thenReturn(Arrays.asList(tipo2, tipo1));

        // When
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorVisualizar(true, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Autenticação", result.get(0).getEspecie());
        assertEquals("Certidão", result.get(1).getEspecie());

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).findByVisualizar(eq(true), any(Sort.class));
    }

    @Test
    void testExistePorId_ExistingId_ShouldReturnTrue() {
        // Given
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = tipoSolicitacaoService.existePorId(1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(1L);
    }

    @Test
    void testExistePorId_NonExistentId_ShouldReturnFalse() {
        // Given
        when(tipoSolicitacaoRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = tipoSolicitacaoService.existePorId(999L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(tipoSolicitacaoRepository, times(1)).existsById(999L);
    }
}