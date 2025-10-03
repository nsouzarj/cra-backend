
package br.adv.cra.service;

import br.adv.cra.dto.OrgaoDTO;
import br.adv.cra.entity.Orgao;
import br.adv.cra.repository.OrgaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgaoServiceMockitoTest {

    @Mock
    private OrgaoRepository orgaoRepository;

    @InjectMocks
    private OrgaoService orgaoService;

    private Orgao orgao1;
    private Orgao orgao2;

    @BeforeEach
    void setUp() {
        orgao1 = new Orgao(1L, "TJSP");
        orgao2 = new Orgao(2L, "TRF3");
    }

    @Test
    void salvar_ShouldSaveAndReturnOrgao() {
        when(orgaoRepository.save(any(Orgao.class))).thenReturn(orgao1);
        Orgao savedOrgao = orgaoService.salvar(orgao1);
        assertNotNull(savedOrgao);
        assertEquals("TJSP", savedOrgao.getDescricao());
        verify(orgaoRepository, times(1)).save(orgao1);
    }

    @Test
    void atualizar_ShouldUpdateOrgao_WhenExists() {
        when(orgaoRepository.existsById(1L)).thenReturn(true);
        when(orgaoRepository.save(any(Orgao.class))).thenReturn(orgao1);
        Orgao updatedOrgao = orgaoService.atualizar(orgao1);
        assertNotNull(updatedOrgao);
        verify(orgaoRepository, times(1)).save(orgao1);
    }

    @Test
    void atualizar_ShouldThrowException_WhenNotExists() {
        when(orgaoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> orgaoService.atualizar(orgao1));
        verify(orgaoRepository, never()).save(any(Orgao.class));
    }

    @Test
    void deletar_ShouldDeleteOrgao_WhenExists() {
        when(orgaoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orgaoRepository).deleteById(1L);
        orgaoService.deletar(1L);
        verify(orgaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotExists() {
        when(orgaoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> orgaoService.deletar(1L));
        verify(orgaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorId_ShouldReturnOrgao_WhenFound() {
        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(orgao1));
        Optional<Orgao> foundOrgao = orgaoService.buscarPorId(1L);
        assertTrue(foundOrgao.isPresent());
        assertEquals("TJSP", foundOrgao.get().getDescricao());
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        when(orgaoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Orgao> foundOrgao = orgaoService.buscarPorId(1L);
        assertFalse(foundOrgao.isPresent());
    }

    @Test
    void listarTodosDTO_ShouldReturnListOfOrgaoDTO() {
        when(orgaoRepository.findAllOrderByDescricao()).thenReturn(Arrays.asList(orgao1, orgao2));
        List<OrgaoDTO> orgaos = orgaoService.listarTodosDTO();
        assertEquals(2, orgaos.size());
        assertEquals("TJSP", orgaos.get(0).getDescricao());
    }

    @Test
    void listarTodos_ShouldReturnListOfOrgao() {
        when(orgaoRepository.findAllOrderByDescricao()).thenReturn(Arrays.asList(orgao1, orgao2));
        List<Orgao> orgaos = orgaoService.listarTodos();
        assertEquals(2, orgaos.size());
        assertEquals("TJSP", orgaos.get(0).getDescricao());
        verify(orgaoRepository, times(1)).findAllOrderByDescricao();
    }

    @Test
    void listarTodosDTO_ShouldThrowException_WhenRepositoryFails() {
        when(orgaoRepository.findAllOrderByDescricao()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orgaoService.listarTodosDTO();
        });

        verify(orgaoRepository, times(1)).findAllOrderByDescricao();
    }

    @Test
    void listarTodos_ShouldThrowException_WhenRepositoryFails() {
        when(orgaoRepository.count()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orgaoService.listarTodos();
        });
    }

    @Test
    void buscarPorDescricao_ShouldThrowException_WhenRepositoryFails() {
        when(orgaoRepository.findByDescricaoContaining(anyString())).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> orgaoService.buscarPorDescricao("Test"));
    }

    @Test
    void existeDescricao_ShouldThrowException_WhenRepositoryFails() {
        when(orgaoRepository.findByDescricaoContaining(anyString())).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> orgaoService.existeDescricao("Test"));
    }

    @Test
    void existeDescricaoParaOutroOrgao_ShouldThrowException_WhenRepositoryFails() {
        when(orgaoRepository.findByDescricaoContaining(anyString())).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> orgaoService.existeDescricaoParaOutroOrgao("Test", 1L));
    }

    @Test
    void buscarPorDescricao_ShouldReturnListOfOrgao() {
        when(orgaoRepository.findByDescricaoContaining("TJSP")).thenReturn(List.of(orgao1));
        List<Orgao> orgaos = orgaoService.buscarPorDescricao("TJSP");
        assertEquals(1, orgaos.size());
        assertEquals("TJSP", orgaos.get(0).getDescricao());
    }

    @Test
    void existeDescricao_ShouldReturnTrue_WhenExists() {
        when(orgaoRepository.findByDescricaoContaining("TJSP")).thenReturn(List.of(orgao1));
        assertTrue(orgaoService.existeDescricao("TJSP"));
    }

    @Test
    void existeDescricao_ShouldReturnFalse_WhenNotExists() {
        when(orgaoRepository.findByDescricaoContaining("TJXX")).thenReturn(Collections.emptyList());
        assertFalse(orgaoService.existeDescricao("TJXX"));
    }

    @Test
    void existeDescricaoParaOutroOrgao_ShouldReturnTrue_WhenOtherOrgaoHasSameDescricao() {
        when(orgaoRepository.findByDescricaoContaining("TRF3")).thenReturn(List.of(orgao2));
        assertTrue(orgaoService.existeDescricaoParaOutroOrgao("TRF3", 1L));
    }

    @Test
    void existeDescricaoParaOutroOrgao_ShouldReturnFalse_WhenNoOrgaoHasDescricao() {
        when(orgaoRepository.findByDescricaoContaining("TRF3")).thenReturn(Collections.emptyList());
        assertFalse(orgaoService.existeDescricaoParaOutroOrgao("TRF3", 1L));
    }

    @Test
    void existeDescricaoParaOutroOrgao_ShouldReturnFalse_WhenSameOrgaoHasDescricao() {
        when(orgaoRepository.findByDescricaoContaining("TJSP")).thenReturn(List.of(orgao1));
        assertFalse(orgaoService.existeDescricaoParaOutroOrgao("TJSP", 1L));
    }
}
