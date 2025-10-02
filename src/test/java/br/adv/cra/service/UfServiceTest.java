package br.adv.cra.service;

import br.adv.cra.dto.UfDTO;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.UfRepository;
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

class UfServiceTest {

    @Mock
    private UfRepository ufRepository;

    private UfService ufService;

    private Uf testUf;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ufService = new UfService(ufRepository);

        // Create test UF
        testUf = new Uf();
        testUf.setId(1L);
        testUf.setSigla("SP");
        testUf.setNome("São Paulo");
    }

    @Test
    void testSalvar_ShouldSaveUf() {
        // Given
        Uf ufToSave = new Uf();
        ufToSave.setSigla("RJ");
        ufToSave.setNome("Rio de Janeiro");

        when(ufRepository.save(any(Uf.class))).thenReturn(testUf);

        // When
        Uf result = ufService.salvar(ufToSave);

        // Then
        assertNotNull(result);
        assertEquals(testUf, result);

        // Verify interactions
        verify(ufRepository, times(1)).save(ufToSave);
    }

    @Test
    void testAtualizar_ExistingUf_ShouldUpdateUf() {
        // Given
        Uf ufToUpdate = new Uf();
        ufToUpdate.setId(1L);
        ufToUpdate.setSigla("SP");
        ufToUpdate.setNome("São Paulo Atualizado");

        when(ufRepository.existsById(1L)).thenReturn(true);
        when(ufRepository.save(any(Uf.class))).thenReturn(ufToUpdate);

        // When
        Uf result = ufService.atualizar(ufToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("São Paulo Atualizado", result.getNome());

        // Verify interactions
        verify(ufRepository, times(1)).existsById(1L);
        verify(ufRepository, times(1)).save(ufToUpdate);
    }

    @Test
    void testAtualizar_NonExistentUf_ShouldThrowException() {
        // Given
        Uf ufToUpdate = new Uf();
        ufToUpdate.setId(999L);
        ufToUpdate.setSigla("XX");
        ufToUpdate.setNome("Non-existent UF");

        when(ufRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ufService.atualizar(ufToUpdate);
        });

        assertEquals("UF não encontrada", exception.getMessage());

        // Verify interactions
        verify(ufRepository, times(1)).existsById(999L);
        verify(ufRepository, never()).save(any(Uf.class));
    }

    @Test
    void testDeletar_ExistingUf_ShouldDeleteUf() {
        // Given
        when(ufRepository.existsById(1L)).thenReturn(true);

        // When
        ufService.deletar(1L);

        // Then
        // Verify interactions
        verify(ufRepository, times(1)).existsById(1L);
        verify(ufRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentUf_ShouldThrowException() {
        // Given
        when(ufRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ufService.deletar(999L);
        });

        assertEquals("UF não encontrada", exception.getMessage());

        // Verify interactions
        verify(ufRepository, times(1)).existsById(999L);
        verify(ufRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingUf_ShouldReturnUf() {
        // Given
        when(ufRepository.findById(1L)).thenReturn(Optional.of(testUf));

        // When
        Optional<Uf> result = ufService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUf, result.get());

        // Verify interactions
        verify(ufRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentUf_ShouldReturnEmpty() {
        // Given
        when(ufRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Uf> result = ufService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(ufRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodasDTO_ShouldReturnAllUfsAsDTOs() {
        // Given
        Uf uf1 = new Uf();
        uf1.setId(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");

        Uf uf2 = new Uf();
        uf2.setId(2L);
        uf2.setSigla("RJ");
        uf2.setNome("Rio de Janeiro");

        when(ufRepository.findAllOrderByNome()).thenReturn(Arrays.asList(uf1, uf2));

        // When
        List<UfDTO> result = ufService.listarTodasDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SP", result.get(0).getSigla());
        assertEquals("São Paulo", result.get(0).getNome());
        assertEquals("RJ", result.get(1).getSigla());
        assertEquals("Rio de Janeiro", result.get(1).getNome());

        // Verify interactions
        verify(ufRepository, times(1)).findAllOrderByNome();
    }

    @Test
    void testListarTodas_ShouldReturnAllUfsOrderedByName() {
        // Given
        Uf uf1 = new Uf();
        uf1.setId(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");

        Uf uf2 = new Uf();
        uf2.setId(2L);
        uf2.setSigla("RJ");
        uf2.setNome("Rio de Janeiro");

        when(ufRepository.findAllOrderByNome()).thenReturn(Arrays.asList(uf1, uf2));

        // When
        List<Uf> result = ufService.listarTodas();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("São Paulo", result.get(0).getNome());
        assertEquals("Rio de Janeiro", result.get(1).getNome());

        // Verify interactions
        verify(ufRepository, times(1)).findAllOrderByNome();
    }

    @Test
    void testListarTodasWithSort_ShouldReturnAllUfsWithCustomSorting() {
        // Given
        Uf uf1 = new Uf();
        uf1.setId(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");

        Uf uf2 = new Uf();
        uf2.setId(2L);
        uf2.setSigla("RJ");
        uf2.setNome("Rio de Janeiro");

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(ufRepository.findAll(sort)).thenReturn(Arrays.asList(uf2, uf1));

        // When
        List<Uf> result = ufService.listarTodas(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rio de Janeiro", result.get(0).getNome());
        assertEquals("São Paulo", result.get(1).getNome());

        // Verify interactions
        verify(ufRepository, times(1)).findAll(sort);
    }

    @Test
    void testBuscarPorSigla_ExistingSigla_ShouldReturnUf() {
        // Given
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(testUf));

        // When
        Optional<Uf> result = ufService.buscarPorSigla("SP");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUf, result.get());
        assertEquals("SP", result.get().getSigla());

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("SP");
    }

    @Test
    void testBuscarPorSigla_NonExistentSigla_ShouldReturnEmpty() {
        // Given
        when(ufRepository.findBySigla("XX")).thenReturn(Optional.empty());

        // When
        Optional<Uf> result = ufService.buscarPorSigla("XX");

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("XX");
    }

    @Test
    void testBuscarPorSigla_ShouldConvertToUpperCase() {
        // Given
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(testUf));

        // When
        Optional<Uf> result = ufService.buscarPorSigla("sp");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUf, result.get());

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("SP");
    }

    @Test
    void testBuscarPorNome_ShouldReturnMatchingUfs() {
        // Given
        Uf uf1 = new Uf();
        uf1.setId(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");

        Uf uf2 = new Uf();
        uf2.setId(2L);
        uf2.setSigla("RJ");
        uf2.setNome("Rio de Janeiro");

        when(ufRepository.findByNomeContaining("São")).thenReturn(Arrays.asList(uf1));

        // When
        List<Uf> result = ufService.buscarPorNome("São");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getNome().contains("São"));

        // Verify interactions
        verify(ufRepository, times(1)).findByNomeContaining("São");
    }

    @Test
    void testBuscarPorNomeWithSort_ShouldReturnMatchingUfsWithCustomSorting() {
        // Given
        Uf uf1 = new Uf();
        uf1.setId(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");

        Uf uf2 = new Uf();
        uf2.setId(2L);
        uf2.setSigla("SC");
        uf2.setNome("Santa Catarina");

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(ufRepository.findByNomeContaining(eq("S"), any(Sort.class))).thenReturn(Arrays.asList(uf2, uf1));

        // When
        List<Uf> result = ufService.buscarPorNome("S", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Santa Catarina", result.get(0).getNome());
        assertEquals("São Paulo", result.get(1).getNome());

        // Verify interactions
        verify(ufRepository, times(1)).findByNomeContaining(eq("S"), any(Sort.class));
    }

    @Test
    void testExisteSigla_ExistingSigla_ShouldReturnTrue() {
        // Given
        when(ufRepository.existsBySigla("SP")).thenReturn(true);

        // When
        boolean result = ufService.existeSigla("SP");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(ufRepository, times(1)).existsBySigla("SP");
    }

    @Test
    void testExisteSigla_NonExistentSigla_ShouldReturnFalse() {
        // Given
        when(ufRepository.existsBySigla("XX")).thenReturn(false);

        // When
        boolean result = ufService.existeSigla("XX");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(ufRepository, times(1)).existsBySigla("XX");
    }

    @Test
    void testExisteSigla_ShouldConvertToUpperCase() {
        // Given
        when(ufRepository.existsBySigla("SP")).thenReturn(true);

        // When
        boolean result = ufService.existeSigla("sp");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(ufRepository, times(1)).existsBySigla("SP");
    }

    @Test
    void testExisteSiglaParaOutraUf_ExistingSiglaForDifferentUf_ShouldReturnTrue() {
        // Given
        Uf uf = new Uf();
        uf.setId(2L); // Different ID
        uf.setSigla("SP");
        uf.setNome("São Paulo");

        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(uf));

        // When
        boolean result = ufService.existeSiglaParaOutraUf("SP", 1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("SP");
    }

    @Test
    void testExisteSiglaParaOutraUf_ExistingSiglaForSameUf_ShouldReturnFalse() {
        // Given
        Uf uf = new Uf();
        uf.setId(1L); // Same ID
        uf.setSigla("SP");
        uf.setNome("São Paulo");

        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(uf));

        // When
        boolean result = ufService.existeSiglaParaOutraUf("SP", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("SP");
    }

    @Test
    void testExisteSiglaParaOutraUf_NonExistentSigla_ShouldReturnFalse() {
        // Given
        when(ufRepository.findBySigla("XX")).thenReturn(Optional.empty());

        // When
        boolean result = ufService.existeSiglaParaOutraUf("XX", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(ufRepository, times(1)).findBySigla("XX");
    }
}