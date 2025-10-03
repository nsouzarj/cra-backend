
package br.adv.cra.service;

import br.adv.cra.dto.UfDTO;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.UfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UfServiceTest {

    @Mock
    private UfRepository ufRepository;

    @InjectMocks
    private UfService ufService;

    private Uf uf1;
    private Uf uf2;

    @BeforeEach
    void setUp() {
        uf1 = new Uf(1L, "SP", "São Paulo");
        uf2 = new Uf(2L, "RJ", "Rio de Janeiro");
    }

    @Test
    void salvar_ShouldSaveAndReturnUf() {
        when(ufRepository.save(any(Uf.class))).thenReturn(uf1);
        Uf savedUf = ufService.salvar(uf1);
        assertNotNull(savedUf);
        assertEquals("SP", savedUf.getSigla());
        verify(ufRepository, times(1)).save(uf1);
    }

    @Test
    void atualizar_ShouldUpdateUf_WhenExists() {
        when(ufRepository.existsById(1L)).thenReturn(true);
        when(ufRepository.save(any(Uf.class))).thenReturn(uf1);
        Uf updatedUf = ufService.atualizar(uf1);
        assertNotNull(updatedUf);
        verify(ufRepository, times(1)).save(uf1);
    }

    @Test
    void atualizar_ShouldThrowException_WhenNotExists() {
        when(ufRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> ufService.atualizar(uf1));
        verify(ufRepository, never()).save(any(Uf.class));
    }

    @Test
    void deletar_ShouldDeleteUf_WhenExists() {
        when(ufRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ufRepository).deleteById(1L);
        ufService.deletar(1L);
        verify(ufRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotExists() {
        when(ufRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> ufService.deletar(1L));
        verify(ufRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorId_ShouldReturnUf_WhenFound() {
        when(ufRepository.findById(1L)).thenReturn(Optional.of(uf1));
        Optional<Uf> foundUf = ufService.buscarPorId(1L);
        assertTrue(foundUf.isPresent());
        assertEquals("SP", foundUf.get().getSigla());
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        when(ufRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Uf> foundUf = ufService.buscarPorId(1L);
        assertFalse(foundUf.isPresent());
    }

    @Test
    void listarTodasDTO_ShouldReturnListOfUfDTO() {
        when(ufRepository.findAllOrderByNome()).thenReturn(Arrays.asList(uf1, uf2));
        List<UfDTO> ufs = ufService.listarTodasDTO();
        assertEquals(2, ufs.size());
        assertEquals("SP", ufs.get(0).getSigla());
    }

    @Test
    void listarTodasDTO_ShouldThrowException_WhenRepositoryFails() {
        when(ufRepository.findAllOrderByNome()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> ufService.listarTodasDTO());
    }

    @Test
    void listarTodas_ShouldReturnListOfUf() {
        when(ufRepository.findAllOrderByNome()).thenReturn(Arrays.asList(uf1, uf2));
        List<Uf> ufs = ufService.listarTodas();
        assertEquals(2, ufs.size());
        assertEquals("São Paulo", ufs.get(0).getNome());
    }

    @Test
    void listarTodas_ShouldThrowException_WhenRepositoryFails() {
        when(ufRepository.findAllOrderByNome()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> ufService.listarTodas());
    }

    @Test
    void listarTodas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "nome");
        when(ufRepository.findAll(sort)).thenReturn(Arrays.asList(uf1, uf2));
        List<Uf> ufs = ufService.listarTodas(sort);
        assertEquals(2, ufs.size());
        verify(ufRepository, times(1)).findAll(sort);
    }

    @Test
    void buscarPorSigla_ShouldReturnUf_WhenFound() {
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(uf1));
        Optional<Uf> foundUf = ufService.buscarPorSigla("SP");
        assertTrue(foundUf.isPresent());
        assertEquals("SP", foundUf.get().getSigla());
    }

    @Test
    void buscarPorSigla_ShouldReturnEmpty_WhenNotFound() {
        when(ufRepository.findBySigla("XX")).thenReturn(Optional.empty());
        Optional<Uf> foundUf = ufService.buscarPorSigla("XX");
        assertFalse(foundUf.isPresent());
    }

    @Test
    void buscarPorNome_ShouldReturnListOfUf() {
        when(ufRepository.findByNomeContaining("Paulo")).thenReturn(List.of(uf1));
        List<Uf> ufs = ufService.buscarPorNome("Paulo");
        assertEquals(1, ufs.size());
        assertEquals("São Paulo", ufs.get(0).getNome());
    }

    @Test
    void buscarPorNome_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("nome");
        when(ufRepository.findByNomeContaining("a", sort)).thenReturn(Arrays.asList(uf2, uf1));
        List<Uf> ufs = ufService.buscarPorNome("a", sort);
        assertEquals(2, ufs.size());
        verify(ufRepository, times(1)).findByNomeContaining("a", sort);
    }

    @Test
    void existeSigla_ShouldReturnTrue_WhenExists() {
        when(ufRepository.existsBySigla("SP")).thenReturn(true);
        assertTrue(ufService.existeSigla("SP"));
    }

    @Test
    void existeSigla_ShouldReturnFalse_WhenNotExists() {
        when(ufRepository.existsBySigla("XX")).thenReturn(false);
        assertFalse(ufService.existeSigla("XX"));
    }

    @Test
    void existeSiglaParaOutraUf_ShouldReturnTrue_WhenOtherUfHasSameSigla() {
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(uf2)); // Different ID
        assertTrue(ufService.existeSiglaParaOutraUf("SP", 1L));
    }

    @Test
    void existeSiglaParaOutraUf_ShouldReturnFalse_WhenNoUfHasSigla() {
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.empty());
        assertFalse(ufService.existeSiglaParaOutraUf("SP", 1L));
    }

    @Test
    void existeSiglaParaOutraUf_ShouldReturnFalse_WhenSameUfHasSigla() {
        when(ufRepository.findBySigla("SP")).thenReturn(Optional.of(uf1)); // Same ID
        assertFalse(ufService.existeSiglaParaOutraUf("SP", 1L));
    }
}
