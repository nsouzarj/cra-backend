package br.adv.cra.service;

import br.adv.cra.dto.ComarcaDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ComarcaServiceTest {

    @Mock
    private ComarcaRepository comarcaRepository;

    private ComarcaService comarcaService;

    private Comarca testComarca;
    private Uf testUf;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        comarcaService = new ComarcaService(comarcaRepository);

        // Create test UF
        testUf = new Uf();
        testUf.setId(1L);
        testUf.setSigla("SP");
        testUf.setNome("São Paulo");

        // Create test comarca
        testComarca = new Comarca();
        testComarca.setId(1L);
        testComarca.setNome("São Paulo");
        testComarca.setAtivo(true);
        testComarca.setUf(testUf);
    }

    @Test
    void testSalvar_ShouldSaveComarca() {
        // Given
        Comarca comarcaToSave = new Comarca();
        comarcaToSave.setNome("Campinas");
        comarcaToSave.setAtivo(true);
        comarcaToSave.setUf(testUf);

        when(comarcaRepository.save(any(Comarca.class))).thenReturn(testComarca);

        // When
        Comarca result = comarcaService.salvar(comarcaToSave);

        // Then
        assertNotNull(result);
        assertEquals(testComarca, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).save(comarcaToSave);
    }

    @Test
    void testAtualizar_ShouldUpdateComarca() {
        // Given
        Comarca comarcaToUpdate = new Comarca();
        comarcaToUpdate.setId(1L);
        comarcaToUpdate.setNome("São Paulo Atualizado");
        comarcaToUpdate.setAtivo(false);
        comarcaToUpdate.setUf(testUf);

        when(comarcaRepository.save(any(Comarca.class))).thenReturn(comarcaToUpdate);

        // When
        Comarca result = comarcaService.atualizar(comarcaToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("São Paulo Atualizado", result.getNome());
        assertFalse(result.isAtivo());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).save(comarcaToUpdate);
    }

    @Test
    void testListarTodas_ShouldReturnAllComarcasOrderedByName() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("A");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("B");
        comarca2.setAtivo(true);

        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.listarTodas();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getNome());
        assertEquals("B", result.get(1).getNome());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAllOrderByNome(any(Sort.class));
    }

    @Test
    void testListarTodasWithPageable_ShouldReturnPageOfComarcas() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("A");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("B");
        comarca2.setAtivo(true);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findAll(pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.listarTodas(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A", result.getContent().get(0).getNome());
        assertEquals("B", result.getContent().get(1).getNome());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListarTodasOrdenadas_ShouldReturnPageOfComarcasOrderedByName() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("A");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("B");
        comarca2.setAtivo(true);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findAllOrderByNome(pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.listarTodasOrdenadas(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A", result.getContent().get(0).getNome());
        assertEquals("B", result.getContent().get(1).getNome());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAllOrderByNome(pageable);
    }

    @Test
    void testContarTodas_ShouldReturnTotalCount() {
        // Given
        when(comarcaRepository.countAllComarcas()).thenReturn(5L);

        // When
        long result = comarcaService.contarTodas();

        // Then
        assertEquals(5L, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).countAllComarcas();
    }

    @Test
    void testListarTodasDTO_ShouldReturnAllComarcasAsDTOs() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenReturn(List.of(comarca1));

        // When
        List<ComarcaDTO> result = comarcaService.listarTodasDTO();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("São Paulo", result.get(0).getNome());
        assertEquals("SP", result.get(0).getUfSigla());
        assertEquals("São Paulo", result.get(0).getUfNome());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAllOrderByNome(any(Sort.class));
    }

    @Test
    void testListarTodasDTOWithPageable_ShouldReturnPageOfComarcasAsDTOs() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findAll(pageable)).thenReturn(comarcaPage);

        // When
        Page<ComarcaDTO> result = comarcaService.listarTodasDTO(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("São Paulo", result.getContent().get(0).getNome());
        assertEquals("SP", result.getContent().get(0).getUfSigla());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListarTodasWithSort_ShouldReturnAllComarcasWithCustomSorting() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("A");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("B");
        comarca2.setAtivo(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(comarcaRepository.findAll(sort)).thenReturn(List.of(comarca2, comarca1));

        // When
        List<Comarca> result = comarcaService.listarTodas(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("B", result.get(0).getNome());
        assertEquals("A", result.get(1).getNome());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findAll(sort);
    }

    @Test
    void testBuscarPorId_ExistingComarca_ShouldReturnComarca() {
        // Given
        when(comarcaRepository.findById(1L)).thenReturn(Optional.of(testComarca));

        // When
        Optional<Comarca> result = comarcaService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testComarca, result.get());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentComarca_ShouldReturnEmpty() {
        // Given
        when(comarcaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Comarca> result = comarcaService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findById(999L);
    }

    @Test
    void testBuscarPorNome_ShouldReturnMatchingComarcas() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("São Bernardo");
        comarca2.setAtivo(true);

        when(comarcaRepository.findByNomeContaining(eq("São"), any(Sort.class))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorNome("São");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getNome().contains("São"));
        assertTrue(result.get(1).getNome().contains("São"));
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByNomeContaining(eq("São"), any(Sort.class));
    }

    @Test
    void testBuscarPorNomeWithPageable_ShouldReturnPageOfMatchingComarcas() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("São Bernardo");
        comarca2.setAtivo(true);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findByNomeContaining(eq("São"), eq(pageable))).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorNome("São", pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).getNome().contains("São"));
        assertTrue(result.getContent().get(1).getNome().contains("São"));
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByNomeContaining(eq("São"), eq(pageable));
    }

    @Test
    void testContarPorNome_ShouldReturnCountOfMatchingComarcas() {
        // Given
        when(comarcaRepository.countByNomeContaining("São")).thenReturn(5L);

        // When
        long result = comarcaService.contarPorNome("São");

        // Then
        assertEquals(5L, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).countByNomeContaining("São");
    }

    @Test
    void testBuscarPorNomeWithSort_ShouldReturnMatchingComarcasWithCustomSorting() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("São Bernardo");
        comarca2.setAtivo(true);

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(comarcaRepository.findByNomeContaining(eq("São"), eq(sort))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorNome("São", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByNomeContaining(eq("São"), eq(sort));
    }

    @Test
    void testBuscarPorUf_ShouldReturnComarcasInState() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        when(comarcaRepository.findByUf(eq(testUf), any(Sort.class))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorUf(testUf);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUf, result.get(0).getUf());
        assertEquals(testUf, result.get(1).getUf());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUf(eq(testUf), any(Sort.class));
    }

    @Test
    void testBuscarPorUfWithPageable_ShouldReturnPageOfComarcasInState() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findByUf(eq(testUf), eq(pageable))).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUf(testUf, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(testUf, result.getContent().get(0).getUf());
        assertEquals(testUf, result.getContent().get(1).getUf());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUf(eq(testUf), eq(pageable));
    }

    @Test
    void testContarPorUf_ShouldReturnCountOfComarcasInState() {
        // Given
        when(comarcaRepository.countByUfId(1L)).thenReturn(10L);

        // When
        long result = comarcaService.contarPorUf(testUf);

        // Then
        assertEquals(10L, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).countByUfId(1L);
    }

    @Test
    void testContarPorUfId_ShouldReturnCountOfComarcasInStateById() {
        // Given
        when(comarcaRepository.countByUfId(1L)).thenReturn(10L);

        // When
        long result = comarcaService.contarPorUfId(1L);

        // Then
        assertEquals(10L, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).countByUfId(1L);
    }

    @Test
    void testBuscarPorUfWithSort_ShouldReturnComarcasInStateWithCustomSorting() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(comarcaRepository.findByUf(eq(testUf), eq(sort))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorUf(testUf, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUf(eq(testUf), eq(sort));
    }

    @Test
    void testBuscarPorUfIdWithPageable_ShouldReturnPageOfComarcasInStateById() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findByUfId(eq(1L), eq(pageable))).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUfId(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(testUf, result.getContent().get(0).getUf());
        assertEquals(testUf, result.getContent().get(1).getUf());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUfId(eq(1L), eq(pageable));
    }

    @Test
    void testBuscarPorUfSigla_ShouldReturnComarcasInStateBySigla() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        when(comarcaRepository.findByUfSiglaOrderByNome(eq("SP"), any(Sort.class))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorUfSigla("SP");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SP", result.get(0).getUf().getSigla());
        assertEquals("SP", result.get(1).getUf().getSigla());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(eq("SP"), any(Sort.class));
    }

    @Test
    void testBuscarPorUfSiglaWithPageable_ShouldReturnPageOfComarcasInStateBySigla() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        Page<Comarca> comarcaPage = new PageImpl<>(List.of(comarca1, comarca2));
        Pageable pageable = PageRequest.of(0, 10);

        when(comarcaRepository.findByUfSiglaOrderByNome(eq("SP"), eq(pageable))).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUfSigla("SP", pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("SP", result.getContent().get(0).getUf().getSigla());
        assertEquals("SP", result.getContent().get(1).getUf().getSigla());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(eq("SP"), eq(pageable));
    }

    @Test
    void testContarPorUfSigla_ShouldReturnCountOfComarcasInStateBySigla() {
        // Given
        when(comarcaRepository.countByUfSigla("SP")).thenReturn(15L);

        // When
        long result = comarcaService.contarPorUfSigla("SP");

        // Then
        assertEquals(15L, result);
        
        // Verify interactions
        verify(comarcaRepository, times(1)).countByUfSigla("SP");
    }

    @Test
    void testBuscarPorUfSiglaWithSort_ShouldReturnComarcasInStateBySiglaWithCustomSorting() {
        // Given
        Comarca comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setAtivo(true);
        comarca1.setUf(testUf);

        Comarca comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setAtivo(true);
        comarca2.setUf(testUf);

        Sort sort = Sort.by(Sort.Direction.DESC, "nome");

        when(comarcaRepository.findByUfSiglaOrderByNome(eq("SP"), eq(sort))).thenReturn(List.of(comarca1, comarca2));

        // When
        List<Comarca> result = comarcaService.buscarPorUfSigla("SP", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify interactions
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(eq("SP"), eq(sort));
    }

    @Test
    void testDeletar_ShouldDeleteComarcaById() {
        // When
        comarcaService.deletar(1L);

        // Then
        // Verify interactions
        verify(comarcaRepository, times(1)).deleteById(1L);
    }
}