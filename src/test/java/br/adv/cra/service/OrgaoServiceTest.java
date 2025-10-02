package br.adv.cra.service;

import br.adv.cra.dto.OrgaoDTO;
import br.adv.cra.entity.Orgao;
import br.adv.cra.repository.OrgaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrgaoServiceTest {

    @Mock
    private OrgaoRepository orgaoRepository;

    private OrgaoService orgaoService;

    private Orgao testOrgao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orgaoService = new OrgaoService(orgaoRepository);

        // Create test orgao
        testOrgao = new Orgao();
        testOrgao.setId(1L);
        testOrgao.setDescricao("Test Orgao");
    }

    @Test
    void testSalvar_ShouldSaveOrgao() {
        // Given
        Orgao orgaoToSave = new Orgao();
        orgaoToSave.setDescricao("New Orgao");

        when(orgaoRepository.save(any(Orgao.class))).thenReturn(testOrgao);

        // When
        Orgao result = orgaoService.salvar(orgaoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testOrgao, result);

        // Verify interactions
        verify(orgaoRepository, times(1)).save(orgaoToSave);
    }

    @Test
    void testAtualizar_ExistingOrgao_ShouldUpdateOrgao() {
        // Given
        Orgao orgaoToUpdate = new Orgao();
        orgaoToUpdate.setId(1L);
        orgaoToUpdate.setDescricao("Updated Orgao");

        when(orgaoRepository.existsById(1L)).thenReturn(true);
        when(orgaoRepository.save(any(Orgao.class))).thenReturn(orgaoToUpdate);

        // When
        Orgao result = orgaoService.atualizar(orgaoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Updated Orgao", result.getDescricao());

        // Verify interactions
        verify(orgaoRepository, times(1)).existsById(1L);
        verify(orgaoRepository, times(1)).save(orgaoToUpdate);
    }

    @Test
    void testAtualizar_NonExistentOrgao_ShouldThrowException() {
        // Given
        Orgao orgaoToUpdate = new Orgao();
        orgaoToUpdate.setId(999L);
        orgaoToUpdate.setDescricao("Non-existent Orgao");

        when(orgaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orgaoService.atualizar(orgaoToUpdate);
        });

        assertEquals("Órgão não encontrado", exception.getMessage());

        // Verify interactions
        verify(orgaoRepository, times(1)).existsById(999L);
        verify(orgaoRepository, never()).save(any(Orgao.class));
    }

    @Test
    void testDeletar_ExistingOrgao_ShouldDeleteOrgao() {
        // Given
        when(orgaoRepository.existsById(1L)).thenReturn(true);

        // When
        orgaoService.deletar(1L);

        // Then
        // Verify interactions
        verify(orgaoRepository, times(1)).existsById(1L);
        verify(orgaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentOrgao_ShouldThrowException() {
        // Given
        when(orgaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orgaoService.deletar(999L);
        });

        assertEquals("Órgão não encontrado", exception.getMessage());

        // Verify interactions
        verify(orgaoRepository, times(1)).existsById(999L);
        verify(orgaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingOrgao_ShouldReturnOrgao() {
        // Given
        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(testOrgao));

        // When
        Optional<Orgao> result = orgaoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOrgao, result.get());

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentOrgao_ShouldReturnEmpty() {
        // Given
        when(orgaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Orgao> result = orgaoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(orgaoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllOrgaosAsDTOs() {
        // Given
        Orgao orgao1 = new Orgao();
        orgao1.setId(1L);
        orgao1.setDescricao("Orgao 1");

        Orgao orgao2 = new Orgao();
        orgao2.setId(2L);
        orgao2.setDescricao("Orgao 2");

        when(orgaoRepository.findAllOrderByDescricao()).thenReturn(Arrays.asList(orgao1, orgao2));

        // When
        List<OrgaoDTO> result = orgaoService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Orgao 1", result.get(0).getDescricao());
        assertEquals("Orgao 2", result.get(1).getDescricao());

        // Verify interactions
        verify(orgaoRepository, times(1)).findAllOrderByDescricao();
    }

    @Test
    void testListarTodos_ShouldReturnAllOrgaos() {
        // Given
        Orgao orgao1 = new Orgao();
        orgao1.setId(1L);
        orgao1.setDescricao("Orgao 1");

        Orgao orgao2 = new Orgao();
        orgao2.setId(2L);
        orgao2.setDescricao("Orgao 2");

        when(orgaoRepository.findAllOrderByDescricao()).thenReturn(Arrays.asList(orgao1, orgao2));

        // When
        List<Orgao> result = orgaoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Orgao 1", result.get(0).getDescricao());
        assertEquals("Orgao 2", result.get(1).getDescricao());

        // Verify interactions
        verify(orgaoRepository, times(1)).findAllOrderByDescricao();
    }

    @Test
    void testBuscarPorDescricao_ShouldReturnMatchingOrgaos() {
        // Given
        Orgao orgao1 = new Orgao();
        orgao1.setId(1L);
        orgao1.setDescricao("Test Orgao 1");

        Orgao orgao2 = new Orgao();
        orgao2.setId(2L);
        orgao2.setDescricao("Test Orgao 2");

        when(orgaoRepository.findByDescricaoContaining("Test")).thenReturn(Arrays.asList(orgao1, orgao2));

        // When
        List<Orgao> result = orgaoService.buscarPorDescricao("Test");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDescricao().contains("Test"));
        assertTrue(result.get(1).getDescricao().contains("Test"));

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("Test");
    }

    @Test
    void testExisteDescricao_ExistingDescription_ShouldReturnTrue() {
        // Given
        Orgao orgao = new Orgao();
        orgao.setId(1L);
        orgao.setDescricao("Test Orgao");

        when(orgaoRepository.findByDescricaoContaining("Test")).thenReturn(Arrays.asList(orgao));

        // When
        boolean result = orgaoService.existeDescricao("Test");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("Test");
    }

    @Test
    void testExisteDescricao_NonExistentDescription_ShouldReturnFalse() {
        // Given
        when(orgaoRepository.findByDescricaoContaining("NonExistent")).thenReturn(Arrays.asList());

        // When
        boolean result = orgaoService.existeDescricao("NonExistent");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("NonExistent");
    }

    @Test
    void testExisteDescricaoParaOutroOrgao_ExistingDescriptionForDifferentOrgao_ShouldReturnTrue() {
        // Given
        Orgao orgao1 = new Orgao();
        orgao1.setId(1L);
        orgao1.setDescricao("Test Orgao");

        Orgao orgao2 = new Orgao();
        orgao2.setId(2L);
        orgao2.setDescricao("Test Orgao");

        when(orgaoRepository.findByDescricaoContaining("Test")).thenReturn(Arrays.asList(orgao1, orgao2));

        // When
        boolean result = orgaoService.existeDescricaoParaOutroOrgao("Test", 1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("Test");
    }

    @Test
    void testExisteDescricaoParaOutroOrgao_ExistingDescriptionForSameOrgao_ShouldReturnFalse() {
        // Given
        Orgao orgao = new Orgao();
        orgao.setId(1L);
        orgao.setDescricao("Test Orgao");

        when(orgaoRepository.findByDescricaoContaining("Test")).thenReturn(Arrays.asList(orgao));

        // When
        boolean result = orgaoService.existeDescricaoParaOutroOrgao("Test", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("Test");
    }

    @Test
    void testExisteDescricaoParaOutroOrgao_NonExistentDescription_ShouldReturnFalse() {
        // Given
        when(orgaoRepository.findByDescricaoContaining("NonExistent")).thenReturn(Arrays.asList());

        // When
        boolean result = orgaoService.existeDescricaoParaOutroOrgao("NonExistent", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(orgaoRepository, times(1)).findByDescricaoContaining("NonExistent");
    }
}