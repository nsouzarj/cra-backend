package br.adv.cra.service;

import br.adv.cra.dto.ComarcaDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.ComarcaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComarcaServiceMockitoTest {

    @Mock
    private ComarcaRepository comarcaRepository;

    @InjectMocks
    private ComarcaService comarcaService;

    private Comarca comarca1;
    private Comarca comarca2;
    private Uf uf;

    @BeforeEach
    void setUp() {
        // Create test data
        uf = new Uf();
        uf.setId(1L);
        uf.setSigla("SP");
        uf.setNome("São Paulo");

        comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("Comarca 1");
        comarca1.setUf(uf);
        comarca1.setAtivo(true);

        comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Comarca 2");
        comarca2.setUf(uf);
        comarca2.setAtivo(false);
    }

    @Test
    void salvar_ShouldSaveAndReturnComarca() {
        // Given
        when(comarcaRepository.save(any(Comarca.class))).thenReturn(comarca1);

        // When
        Comarca result = comarcaService.salvar(comarca1);

        // Then
        assertNotNull(result);
        assertEquals(comarca1.getId(), result.getId());
        assertEquals(comarca1.getNome(), result.getNome());
        verify(comarcaRepository, times(1)).save(comarca1);
    }

    @Test
    void salvar_ShouldThrowException_WhenRepositoryFails() {
        // Given
        when(comarcaRepository.save(any(Comarca.class))).thenThrow(new RuntimeException("Database error"));
        // Then
        assertThrows(RuntimeException.class, () -> comarcaService.salvar(comarca1));
    }

    @Test
    void atualizar_ShouldUpdateAndReturnComarca() {
        // Given
        when(comarcaRepository.save(any(Comarca.class))).thenReturn(comarca1);

        // When
        Comarca result = comarcaService.atualizar(comarca1);

        // Then
        assertNotNull(result);
        assertEquals(comarca1.getId(), result.getId());
        assertEquals(comarca1.getNome(), result.getNome());
        verify(comarcaRepository, times(1)).save(comarca1);
    }

    @Test
    void atualizar_ShouldThrowException_WhenRepositoryFails() {
        // Given
        when(comarcaRepository.save(any(Comarca.class))).thenThrow(new RuntimeException("Database error"));
        // Then
        assertThrows(RuntimeException.class, () -> comarcaService.atualizar(comarca1));
    }

    @Test
    void listarTodas_ShouldReturnAllComarcasOrderedByName() {
        // Given
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.listarTodas();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca1.getNome(), result.get(0).getNome());
        assertEquals(comarca2.getNome(), result.get(1).getNome());
        verify(comarcaRepository, times(1)).findAllOrderByNome(any(Sort.class));
    }

    @Test
    void listarTodas_ShouldThrowException_WhenRepositoryFails() {
        // Given
        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenThrow(new RuntimeException("Database error"));
        // Then
        assertThrows(RuntimeException.class, () -> comarcaService.listarTodas());
    }

    @Test
    void deletar_ShouldDeleteComarcaById() {
        // Given
        Long id = 1L;
        doNothing().when(comarcaRepository).deleteById(eq(id));

        // When
        comarcaService.deletar(id);

        // Then
        verify(comarcaRepository, times(1)).deleteById(eq(id));
    }

    @Test
    void deletar_ShouldThrowException_WhenRepositoryFails() {
        // Given
        Long id = 1L;
        doThrow(new RuntimeException("Database error")).when(comarcaRepository).deleteById(id);
        // Then
        assertThrows(RuntimeException.class, () -> comarcaService.deletar(id));
    }

    @Test
    void buscarPorId_ShouldReturnComarca_WhenFound() {
        // Given
        Long id = 1L;
        when(comarcaRepository.findById(id)).thenReturn(Optional.of(comarca1));

        // When
        Optional<Comarca> result = comarcaService.buscarPorId(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(comarca1.getId(), result.get().getId());
        verify(comarcaRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        // Given
        Long id = 99L;
        when(comarcaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Comarca> result = comarcaService.buscarPorId(id);

        // Then
        assertFalse(result.isPresent());
        verify(comarcaRepository, times(1)).findById(id);
    }

    @Test
    void listarTodas_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findAll(pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.listarTodas(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(comarca1.getNome(), result.getContent().get(0).getNome());
        verify(comarcaRepository, times(1)).findAll(pageable);
    }

    @Test
    void contarTodas_ShouldReturnTotalCount() {
        // Given
        when(comarcaRepository.countAllComarcas()).thenReturn(2L);

        // When
        long result = comarcaService.contarTodas();

        // Then
        assertEquals(2L, result);
        verify(comarcaRepository, times(1)).countAllComarcas();
    }

    @Test
    void listarTodasDTO_ShouldReturnListOfComarcaDTO() {
        // Given
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenReturn(comarcas);

        // When
        List<ComarcaDTO> result = comarcaService.listarTodasDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca1.getNome(), result.get(0).getNome());
        assertEquals(comarca1.getUf().getSigla(), result.get(0).getUfSigla());
        verify(comarcaRepository, times(1)).findAllOrderByNome(any(Sort.class));
    }

    @Test
    void buscarPorNome_ShouldReturnListOfComarcas() {
        // Given
        String nome = "Comarca";
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findByNomeContaining(eq(nome), any(Sort.class))).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorNome(nome);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(comarcaRepository, times(1)).findByNomeContaining(eq(nome), any(Sort.class));
    }

    @Test
    void buscarPorUf_ShouldReturnListOfComarcas() {
        // Given
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findByUf(eq(uf), any(Sort.class))).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorUf(uf);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(comarcaRepository, times(1)).findByUf(eq(uf), any(Sort.class));
    }

    @Test
    void contarPorUfId_ShouldReturnCount() {
        // Given
        Long ufId = 1L;
        when(comarcaRepository.countByUfId(ufId)).thenReturn(5L);

        // When
        long result = comarcaService.contarPorUfId(ufId);

        // Then
        assertEquals(5L, result);
        verify(comarcaRepository, times(1)).countByUfId(ufId);
    }

    @Test
    void listarTodasOrdenadas_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findAllOrderByNome(pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.listarTodasOrdenadas(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(comarcaRepository, times(1)).findAllOrderByNome(pageable);
    }

    @Test
    void listarTodasDTO_WithPageable_ShouldReturnPageOfComarcaDTO() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findAll(pageable)).thenReturn(comarcaPage);

        // When
        Page<ComarcaDTO> result = comarcaService.listarTodasDTO(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(comarca1.getNome(), result.getContent().get(0).getNome());
        verify(comarcaRepository, times(1)).findAll(pageable);
    }

    @Test
    void listarTodas_WithSort_ShouldReturnSortedList() {
        // Given
        Sort sort = Sort.by(Sort.Direction.DESC, "nome");
        List<Comarca> comarcas = Arrays.asList(comarca2, comarca1);
        when(comarcaRepository.findAll(sort)).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.listarTodas(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca2.getNome(), result.get(0).getNome());
        verify(comarcaRepository, times(1)).findAll(sort);
    }

    @Test
    void buscarPorNome_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        String nome = "Comarca";
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findByNomeContaining(nome, pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorNome(nome, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(comarcaRepository, times(1)).findByNomeContaining(nome, pageable);
    }

    @Test
    void contarPorNome_ShouldReturnCount() {
        // Given
        String nome = "Comarca";
        when(comarcaRepository.countByNomeContaining(nome)).thenReturn(2L);

        // When
        long result = comarcaService.contarPorNome(nome);

        // Then
        assertEquals(2L, result);
        verify(comarcaRepository, times(1)).countByNomeContaining(nome);
    }

    @Test
    void buscarPorUf_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findByUf(uf, pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUf(uf, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(comarcaRepository, times(1)).findByUf(uf, pageable);
    }

    @Test
    void contarPorUf_ShouldReturnCount() {
        // Given
        when(comarcaRepository.countByUfId(uf.getId())).thenReturn(2L);

        // When
        long result = comarcaService.contarPorUf(uf);

        // Then
        assertEquals(2L, result);
        verify(comarcaRepository, times(1)).countByUfId(uf.getId());
    }

    @Test
    void buscarPorUfId_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        Long ufId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findByUfId(ufId, pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUfId(ufId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(comarcaRepository, times(1)).findByUfId(ufId, pageable);
    }

    @Test
    void buscarPorUfSigla_WithPageable_ShouldReturnPageOfComarcas() {
        // Given
        String sigla = "SP";
        Pageable pageable = PageRequest.of(0, 10);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        Page<Comarca> comarcaPage = new PageImpl<>(comarcas, pageable, comarcas.size());
        when(comarcaRepository.findByUfSiglaOrderByNome(sigla, pageable)).thenReturn(comarcaPage);

        // When
        Page<Comarca> result = comarcaService.buscarPorUfSigla(sigla, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(sigla, pageable);
    }

    @Test
    void contarPorUfSigla_ShouldReturnCount() {
        // Given
        String sigla = "SP";
        when(comarcaRepository.countByUfSigla(sigla)).thenReturn(2L);

        // When
        long result = comarcaService.contarPorUfSigla(sigla);

        // Then
        assertEquals(2L, result);
        verify(comarcaRepository, times(1)).countByUfSigla(sigla);
    }

    @Test
    void listarTodasDTO_ShouldHandleNullUf() {
        // Given
        comarca2.setUf(null);
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findAllOrderByNome(any(Sort.class))).thenReturn(comarcas);

        // When
        List<ComarcaDTO> result = comarcaService.listarTodasDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getUfSigla());
        assertNull(result.get(1).getUfSigla());
        verify(comarcaRepository, times(1)).findAllOrderByNome(any(Sort.class));
    }

    @Test
    void buscarPorNome_WithSort_ShouldReturnSortedList() {
        // Given
        String nome = "Comarca";
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Comarca> comarcas = Arrays.asList(comarca2, comarca1);
        when(comarcaRepository.findByNomeContaining(nome, sort)).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorNome(nome, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca2.getId(), result.get(0).getId());
        verify(comarcaRepository, times(1)).findByNomeContaining(nome, sort);
    }

    @Test
    void buscarPorUf_WithSort_ShouldReturnSortedList() {
        // Given
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Comarca> comarcas = Arrays.asList(comarca2, comarca1);
        when(comarcaRepository.findByUf(uf, sort)).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorUf(uf, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca2.getId(), result.get(0).getId());
        verify(comarcaRepository, times(1)).findByUf(uf, sort);
    }

    @Test
    void buscarPorUfSigla_ShouldReturnListOfComarcas() {
        // Given
        String sigla = "SP";
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaRepository.findByUfSiglaOrderByNome(eq(sigla), any(Sort.class))).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorUfSigla(sigla);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(eq(sigla), any(Sort.class));
    }

    @Test
    void buscarPorUfSigla_WithSort_ShouldReturnSortedList() {
        // Given
        String sigla = "SP";
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Comarca> comarcas = Arrays.asList(comarca2, comarca1);
        when(comarcaRepository.findByUfSiglaOrderByNome(sigla, sort)).thenReturn(comarcas);

        // When
        List<Comarca> result = comarcaService.buscarPorUfSigla(sigla, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comarca2.getId(), result.get(0).getId());
        verify(comarcaRepository, times(1)).findByUfSiglaOrderByNome(sigla, sort);
    }
}