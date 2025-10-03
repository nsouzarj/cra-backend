package br.adv.cra.service;

import br.adv.cra.dto.ProcessoDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import br.adv.cra.repository.ComarcaRepository;
import br.adv.cra.repository.OrgaoRepository;
import br.adv.cra.repository.ProcessoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessoServiceMockitoTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private ComarcaRepository comarcaRepository;

    @Mock
    private OrgaoRepository orgaoRepository;

    @InjectMocks
    private ProcessoService processoService;

    private Processo processo1;
    private Processo processo2;
    private ProcessoDTO processoDTO;
    private Comarca comarca;
    private Orgao orgao;

    @BeforeEach
    void setUp() {
        comarca = new Comarca(1L, "Comarca Teste", null, true, null);
        orgao = new Orgao(1L, "Orgao Teste");

        processo1 = new Processo();
        processo1.setId(1L);
        processo1.setNumeroprocesso("1111111-11.2023.1.11.1111");
        processo1.setParte("Parte A");
        processo1.setAdverso("Adverso B");
        processo1.setStatus("Ativo");
        processo1.setComarca(comarca);
        processo1.setOrgao(orgao);

        processo2 = new Processo();
        processo2.setId(2L);
        processo2.setNumeroprocesso("2222222-22.2023.2.22.2222");
        processo2.setParte("Parte C");
        processo2.setAdverso("Adverso D");
        processo2.setStatus("Concluído");

        processoDTO = new ProcessoDTO();
        processoDTO.setNumeroprocesso("3333333-33.2023.3.33.3333");
        processoDTO.setComarcaId(1L);
        processoDTO.setOrgaoId(1L);
    }

    @Test
    void salvar_ShouldSaveAndReturnProcesso() {
        when(processoRepository.save(any(Processo.class))).thenReturn(processo1);
        Processo saved = processoService.salvar(processo1);
        assertNotNull(saved);
        assertEquals(processo1.getNumeroprocesso(), saved.getNumeroprocesso());
        verify(processoRepository, times(1)).save(processo1);
    }

    @Test
    void salvar_ShouldSaveWithNullRelations() {
        Processo processoSemRelacao = new Processo();
        processoSemRelacao.setId(3L);
        processoSemRelacao.setNumeroprocesso("999");
        when(processoRepository.save(processoSemRelacao)).thenReturn(processoSemRelacao);

        Processo saved = processoService.salvar(processoSemRelacao);

        assertNotNull(saved);
        assertNull(saved.getComarca());
        assertNull(saved.getOrgao());
    }

    @Test
    void salvarComDTO_ShouldSaveAndReturnProcesso() {
        when(comarcaRepository.findById(1L)).thenReturn(Optional.of(comarca));
        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(orgao));
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Processo saved = processoService.salvarComDTO(processoDTO);

        assertNotNull(saved);
        assertEquals(processoDTO.getNumeroprocesso(), saved.getNumeroprocesso());
        assertEquals(comarca, saved.getComarca());
        assertEquals(orgao, saved.getOrgao());
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void salvarComDTO_ShouldSaveWithNullRelations_WhenIdsAreNull() {
        processoDTO.setComarcaId(null);
        processoDTO.setOrgaoId(null);
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Processo saved = processoService.salvarComDTO(processoDTO);

        assertNotNull(saved);
        assertNull(saved.getComarca());
        assertNull(saved.getOrgao());
        verify(comarcaRepository, never()).findById(any());
        verify(orgaoRepository, never()).findById(any());
    }

    @Test
    void salvarComDTO_ShouldThrowException_WhenComarcaNotFound() {
        when(orgaoRepository.findById(1L)).thenReturn(Optional.of(orgao));
        when(comarcaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> processoService.salvarComDTO(processoDTO));
    }

    @Test
    void salvarComDTO_ShouldThrowException_WhenOrgaoNotFound() {
        when(orgaoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> processoService.salvarComDTO(processoDTO));
    }

    @Test
    void atualizar_ShouldUpdateAndReturnProcesso() {
        when(processoRepository.existsById(1L)).thenReturn(true);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo1);
        Processo updated = processoService.atualizar(processo1);
        assertNotNull(updated);
        assertEquals(processo1.getId(), updated.getId());
        verify(processoRepository, times(1)).save(processo1);
    }

    @Test
    void atualizar_ShouldUpdateWithNullRelations_WhenRelationsAreNull() {
        processo1.setComarca(null);
        processo1.setOrgao(null);
        when(processoRepository.existsById(1L)).thenReturn(true);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo1);

        Processo updated = processoService.atualizar(processo1);

        assertNotNull(updated);
        assertNull(updated.getComarca());
        assertNull(updated.getOrgao());
    }

    @Test
    void atualizar_ShouldThrowException_WhenProcessoNotFound() {
        when(processoRepository.existsById(99L)).thenReturn(false);
        processo1.setId(99L);
        assertThrows(RuntimeException.class, () -> processoService.atualizar(processo1));
    }

    @Test
    void deletar_ShouldCallDeleteById() {
        when(processoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(processoRepository).deleteById(1L);
        processoService.deletar(1L);
        verify(processoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotFound() {
        when(processoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> processoService.deletar(99L));
        verify(processoRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorId_ShouldReturnProcesso_WhenFound() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo1));
        Optional<Processo> found = processoService.buscarPorId(1L);
        assertTrue(found.isPresent());
        assertEquals(processo1.getId(), found.get().getId());
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        when(processoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Processo> found = processoService.buscarPorId(1L);
        assertFalse(found.isPresent());
    }

    @Test
    void listarTodos_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findAll(pageable)).thenReturn(page);
        Page<Processo> result = processoService.listarTodos(pageable);
        assertEquals(1, result.getTotalElements());
        verify(processoRepository, times(1)).findAll(pageable);
    }

    @Test
    void listarTodosDTO_ShouldReturnPageOfDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findAll(pageable)).thenReturn(page);

        Page<ProcessoDTO> result = processoService.listarTodosDTO(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(processo1.getNumeroprocesso(), result.getContent().get(0).getNumeroprocesso());
    }

    @Test
    void listarTodos_ShouldReturnList() {
        when(processoRepository.findAll(any(Sort.class))).thenReturn(List.of(processo1));
        List<Processo> result = processoService.listarTodos();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void listarTodos_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findAll(sort)).thenReturn(List.of(processo1));
        List<Processo> result = processoService.listarTodos(sort);
        assertFalse(result.isEmpty());
        verify(processoRepository).findAll(sort);
    }

    @Test
    void listarTodosDTO_ShouldReturnListOfDTO() {
        when(processoRepository.findAll(any(Sort.class))).thenReturn(List.of(processo1));
        List<ProcessoDTO> result = processoService.listarTodosDTO();
        assertEquals(1, result.size());
    }

    @Test
    void contarTodos_ShouldReturnCount() {
        when(processoRepository.countAllProcessos()).thenReturn(2L);
        long count = processoService.contarTodos();
        assertEquals(2L, count);
        verify(processoRepository, times(1)).countAllProcessos();
    }

    @Test
    void existeNumeroProcesso_ShouldReturnTrue_WhenExists() {
        when(processoRepository.existsByNumeroprocesso(anyString())).thenReturn(true);
        assertTrue(processoService.existeNumeroProcesso("1111111-11.2023.1.11.1111"));
    }

    @Test
    void existeNumeroProcesso_ShouldReturnFalse_WhenNotExists() {
        when(processoRepository.existsByNumeroprocesso(anyString())).thenReturn(false);
        assertFalse(processoService.existeNumeroProcesso("000"));
    }

    @Test
    void existeNumeroProcessoParaOutroProcesso_ShouldReturnTrue_WhenExistsForOther() {
        when(processoRepository.findByNumeroprocesso(processo2.getNumeroprocesso())).thenReturn(Optional.of(processo2));
        assertTrue(processoService.existeNumeroProcessoParaOutroProcesso(processo2.getNumeroprocesso(), processo1.getId()));
    }

    @Test
    void existeNumeroProcessoParaOutroProcesso_ShouldReturnFalse_WhenExistsForSame() {
        when(processoRepository.findByNumeroprocesso(processo1.getNumeroprocesso())).thenReturn(Optional.of(processo1));
        assertFalse(processoService.existeNumeroProcessoParaOutroProcesso(processo1.getNumeroprocesso(), processo1.getId()));
    }

    @Test
    void existeNumeroProcessoParaOutroProcesso_ShouldReturnFalse_WhenNotExists() {
        when(processoRepository.findByNumeroprocesso(anyString())).thenReturn(Optional.empty());
        assertFalse(processoService.existeNumeroProcessoParaOutroProcesso("000", 1L));
    }

    @Test
    void buscarPorNumeroProcesso_ShouldReturnProcesso() {
        when(processoRepository.findByNumeroprocesso(processo1.getNumeroprocesso())).thenReturn(Optional.of(processo1));
        Optional<Processo> found = processoService.buscarPorNumeroProcesso(processo1.getNumeroprocesso());
        assertTrue(found.isPresent());
        assertEquals(processo1.getNumeroprocesso(), found.get().getNumeroprocesso());
    }

    @Test
    void buscarPorNumeroProcessoPesquisa_ShouldReturnList() {
        when(processoRepository.findByNumeroprocessopesqContaining(anyString(), any(Sort.class)))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorNumeroProcessoPesquisa("111");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorNumeroProcessoPesquisa_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByNumeroprocessopesqContaining("111", sort))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorNumeroProcessoPesquisa("111", sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByNumeroprocessopesqContaining("111", sort);
    }

    @Test
    void buscarPorNumeroProcessoPesquisa_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByNumeroprocessopesqContaining("111", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorNumeroProcessoPesquisa("111", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorParte_ShouldReturnList() {
        when(processoRepository.findByParteContaining(anyString(), any(Sort.class)))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorParte("Parte A");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorParte_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByParteContaining("Parte A", sort))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorParte("Parte A", sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByParteContaining("Parte A", sort);
    }

    @Test
    void buscarPorParte_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByParteContaining("Parte A", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorParte("Parte A", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorAdverso_ShouldReturnList() {
        when(processoRepository.findByAdversoContaining(anyString(), any(Sort.class)))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorAdverso("Adverso B");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorAdverso_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByAdversoContaining("Adverso B", sort))
                .thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorAdverso("Adverso B", sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByAdversoContaining("Adverso B", sort);
    }

    @Test
    void buscarPorAdverso_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByAdversoContaining("Adverso B", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorAdverso("Adverso B", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorStatus_ShouldReturnList() {
        when(processoRepository.findByStatus(anyString(), any(Sort.class))).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorStatus("Ativo");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorStatus_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByStatus("Ativo", sort)).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorStatus("Ativo", sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByStatus("Ativo", sort);
    }

    @Test
    void buscarPorStatus_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByStatus("Ativo", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorStatus("Ativo", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorAssunto_ShouldReturnList() {
        when(processoRepository.findByAssuntoContaining(anyString(), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        List<Processo> result = processoService.buscarPorAssunto("Assunto Teste");
        assertTrue(result.isEmpty());
    }

    @Test
    void buscarPorAssunto_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByAssuntoContaining("Assunto Teste", sort)).thenReturn(Collections.emptyList());
        List<Processo> result = processoService.buscarPorAssunto("Assunto Teste", sort);
        assertTrue(result.isEmpty());
    }

    @Test
    void buscarPorAssunto_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(Collections.emptyList());
        when(processoRepository.findByAssuntoContaining("Assunto Teste", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorAssunto("Assunto Teste", pageable);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void buscarPorProcessoEletronico_ShouldReturnList() {
        when(processoRepository.findByProceletronico(anyString(), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        List<Processo> result = processoService.buscarPorProcessoEletronico("E-123");
        assertTrue(result.isEmpty());
    }

    @Test
    void buscarPorProcessoEletronico_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByProceletronico("E-123", sort)).thenReturn(Collections.emptyList());
        List<Processo> result = processoService.buscarPorProcessoEletronico("E-123", sort);
        assertTrue(result.isEmpty());
    }

    @Test
    void buscarPorProcessoEletronico_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(Collections.emptyList());
        when(processoRepository.findByProceletronico("E-123", pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorProcessoEletronico("E-123", pageable);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void buscarPorComarca_ShouldReturnList() {
        when(processoRepository.findByComarca(any(Comarca.class), any(Sort.class))).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorComarca(comarca);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorComarca_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByComarca(comarca, sort)).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorComarca(comarca, sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByComarca(comarca, sort);
    }

    @Test
    void buscarPorComarca_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByComarca(comarca, pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorComarca(comarca, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorOrgao_ShouldReturnList() {
        when(processoRepository.findByOrgao(any(Orgao.class), any(Sort.class))).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorOrgao(orgao);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorOrgao_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("numeroprocesso");
        when(processoRepository.findByOrgao(orgao, sort)).thenReturn(List.of(processo1));
        List<Processo> result = processoService.buscarPorOrgao(orgao, sort);
        assertEquals(1, result.size());
        verify(processoRepository).findByOrgao(orgao, sort);
    }

    @Test
    void buscarPorOrgao_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processo> page = new PageImpl<>(List.of(processo1));
        when(processoRepository.findByOrgao(orgao, pageable)).thenReturn(page);
        Page<Processo> result = processoService.buscarPorOrgao(orgao, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void contarPorStatus_ShouldReturnCount() {
        when(processoRepository.countByStatus("Ativo")).thenReturn(1L);
        Long count = processoService.contarPorStatus("Ativo");
        assertEquals(1L, count);
    }

    @Test
    void contarPorStatus_ShouldReturnZeroWhenNull() {
        when(processoRepository.countByStatus("Inexistente")).thenReturn(null);
        Long count = processoService.contarPorStatus("Inexistente");
        assertEquals(0L, count);
    }

    @Test
    void toDTO_ShouldHandleNullRelations() {
        // Configura um processo com relações nulas
        Processo processoSemRelacao = new Processo();
        processoSemRelacao.setId(99L);
        processoSemRelacao.setNumeroprocesso("999");
        processoSemRelacao.setComarca(null);
        processoSemRelacao.setOrgao(null);

        // Testa a conversão para DTO através de um método público
        when(processoRepository.findAll(any(Sort.class))).thenReturn(List.of(processoSemRelacao));
        List<ProcessoDTO> dtos = processoService.listarTodosDTO();

        assertFalse(dtos.isEmpty());
        assertNull(dtos.get(0).getComarcaId());
        assertNull(dtos.get(0).getOrgaoId());
    }
}