package br.adv.cra.service;

import br.adv.cra.dto.TipoSolicitacaoDTO;
import br.adv.cra.entity.TipoSolicitacao;
import br.adv.cra.repository.TipoSolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoSolicitacaoServiceMockitoTest {

    @Mock
    private TipoSolicitacaoRepository tipoSolicitacaoRepository;

    @InjectMocks
    private TipoSolicitacaoService tipoSolicitacaoService;

    private TipoSolicitacao tipo1;
    private TipoSolicitacao tipo2;

    @BeforeEach
    void setUp() {
        tipo1 = new TipoSolicitacao(1L, "Audiência", "Audiência de conciliação", "A", true);
        tipo2 = new TipoSolicitacao(2L, "Diligência", "Cópia de processo", "D", true);
    }

    @Test
    void salvar_ShouldSaveAndReturn() {
        when(tipoSolicitacaoRepository.save(any(TipoSolicitacao.class))).thenReturn(tipo1);
        TipoSolicitacao saved = tipoSolicitacaoService.salvar(tipo1);
        assertNotNull(saved);
        assertEquals("Audiência", saved.getEspecie());
        verify(tipoSolicitacaoRepository, times(1)).save(tipo1);
    }

    @Test
    void atualizar_ShouldUpdate_WhenExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);
        when(tipoSolicitacaoRepository.save(any(TipoSolicitacao.class))).thenReturn(tipo1);
        TipoSolicitacao updated = tipoSolicitacaoService.atualizar(tipo1);
        assertNotNull(updated);
        verify(tipoSolicitacaoRepository, times(1)).save(tipo1);
    }

    @Test
    void atualizar_ShouldThrowException_WhenNotExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> tipoSolicitacaoService.atualizar(tipo1));
        verify(tipoSolicitacaoRepository, never()).save(any(TipoSolicitacao.class));
    }

    @Test
    void deletar_ShouldDelete_WhenExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tipoSolicitacaoRepository).deleteById(1L);
        tipoSolicitacaoService.deletar(1L);
        verify(tipoSolicitacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> tipoSolicitacaoService.deletar(1L));
        verify(tipoSolicitacaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorId_ShouldReturn_WhenFound() {
        when(tipoSolicitacaoRepository.findById(1L)).thenReturn(Optional.of(tipo1));
        Optional<TipoSolicitacao> found = tipoSolicitacaoService.buscarPorId(1L);
        assertTrue(found.isPresent());
        assertEquals("Audiência", found.get().getEspecie());
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        when(tipoSolicitacaoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<TipoSolicitacao> found = tipoSolicitacaoService.buscarPorId(1L);
        assertFalse(found.isPresent());
    }

    @Test
    void listarTodosDTO_ShouldReturnListOfDTO() {
        when(tipoSolicitacaoRepository.findAll()).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacaoDTO> dtos = tipoSolicitacaoService.listarTodosDTO();
        assertEquals(2, dtos.size());
        assertEquals("Audiência", dtos.get(0).getEspecie());
    }

    @Test
    void listarTodos_ShouldReturnList() {
        when(tipoSolicitacaoRepository.findAll()).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodos();
        assertEquals(2, result.size());
    }

    @Test
    void listarTodos_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("especie");
        when(tipoSolicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodos(sort);
        assertEquals(2, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findAll(sort);
    }

    @Test
    void listarTodosOrdenados_ShouldReturnOrderedList() {
        when(tipoSolicitacaoRepository.findAllOrderByEspecie()).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodosOrdenados();
        assertEquals(2, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findAllOrderByEspecie();
    }

    @Test
    void buscarPorEspecie_ShouldReturnList() {
        when(tipoSolicitacaoRepository.findByEspecieContaining("Audiência")).thenReturn(List.of(tipo1));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorEspecie("Audiência");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorEspecie_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(tipoSolicitacaoRepository.findByEspecieContaining("a", sort)).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorEspecie("a", sort);
        assertEquals(2, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findByEspecieContaining("a", sort);
    }

    @Test
    void buscarPorDescricao_ShouldReturnList() {
        when(tipoSolicitacaoRepository.findByDescricaoContaining("conciliação")).thenReturn(List.of(tipo1));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorDescricao("conciliação");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorDescricao_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(tipoSolicitacaoRepository.findByDescricaoContaining("processo", sort)).thenReturn(List.of(tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorDescricao("processo", sort);
        assertEquals(1, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findByDescricaoContaining("processo", sort);
    }

    @Test
    void buscarPorTipo_ShouldReturnList() {
        when(tipoSolicitacaoRepository.findByTipo("A")).thenReturn(List.of(tipo1));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorTipo("A");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorTipo_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        when(tipoSolicitacaoRepository.findByTipo("A", sort)).thenReturn(List.of(tipo1));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorTipo("A", sort);
        assertEquals(1, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findByTipo("A", sort);
    }

    @Test
    void buscarPorVisualizar_ShouldReturnList() {
        when(tipoSolicitacaoRepository.findByVisualizar(true)).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorVisualizar(true);
        assertEquals(2, result.size());
    }

    @Test
    void buscarPorVisualizar_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(tipoSolicitacaoRepository.findByVisualizar(true, sort)).thenReturn(Arrays.asList(tipo1, tipo2));
        List<TipoSolicitacao> result = tipoSolicitacaoService.buscarPorVisualizar(true, sort);
        assertEquals(2, result.size());
        verify(tipoSolicitacaoRepository, times(1)).findByVisualizar(true, sort);
    }

    @Test
    void existePorId_ShouldReturnTrue_WhenExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(true);
        assertTrue(tipoSolicitacaoService.existePorId(1L));
    }

    @Test
    void existePorId_ShouldReturnFalse_WhenNotExists() {
        when(tipoSolicitacaoRepository.existsById(1L)).thenReturn(false);
        assertFalse(tipoSolicitacaoService.existePorId(1L));
    }

    // Testes de sobrecarga com Sort
    @Test
    void listarTodosOrdenados_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "especie");
        when(tipoSolicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(tipo2, tipo1));
        List<TipoSolicitacao> result = tipoSolicitacaoService.listarTodosOrdenados(sort);
        assertEquals(2, result.size());
        assertEquals("Diligência", result.get(0).getEspecie());
        verify(tipoSolicitacaoRepository, times(1)).findAll(sort);
    }
}