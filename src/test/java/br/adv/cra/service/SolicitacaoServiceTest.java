package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoDTO;
import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.*;
import br.adv.cra.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private StatusSolicitacaoRepository statusSolicitacaoRepository;

    @Mock
    private SoliArquivoRepository soliArquivoRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    private Solicitacao solicitacao;
    private StatusSolicitacao statusPendente;
    private StatusSolicitacao statusConcluida;
    private Usuario usuario;
    private Processo processo;
    private Comarca comarca;
    private Correspondente correspondente;
    private TipoSolicitacao tipoSolicitacao;


    @BeforeEach
    void setUp() {
        statusPendente = new StatusSolicitacao(1L, "Pendente");
        statusConcluida = new StatusSolicitacao(2L, "Concluída");

        solicitacao = new Solicitacao();
        solicitacao.setId(1L);
        solicitacao.setDatasolicitacao(LocalDateTime.now());
        solicitacao.setStatusSolicitacao(statusPendente);
        solicitacao.setObservacao("Observação inicial");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomecompleto("Test User");

        processo = new Processo();
        processo.setId(1L);
        processo.setNumeroprocesso("12345");

        comarca = new Comarca();
        comarca.setId(1L);
        comarca.setNome("Test Comarca");

        correspondente = new Correspondente();
        correspondente.setId(1L);
        correspondente.setNome("Corresp Test");
    }

    @Test
    void salvar_ShouldSetDateAndSave() {
        Solicitacao novaSolicitacao = new Solicitacao();
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(novaSolicitacao);

        Solicitacao saved = solicitacaoService.salvar(novaSolicitacao);

        assertNotNull(saved.getDatasolicitacao());
        verify(solicitacaoRepository, times(1)).save(novaSolicitacao);
    }

    @Test
    void salvar_ShouldUseProvidedDate_WhenNotNull() {
        LocalDateTime specificDate = LocalDateTime.of(2023, 1, 1, 10, 0);
        solicitacao.setDatasolicitacao(specificDate);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);
        Solicitacao saved = solicitacaoService.salvar(solicitacao);
        assertEquals(specificDate, saved.getDatasolicitacao());
    }

    @Test
    void atualizar_ShouldUpdate_WhenExists() {
        when(solicitacaoRepository.existsById(1L)).thenReturn(true);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao updated = solicitacaoService.atualizar(solicitacao);

        assertNotNull(updated);
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }

    @Test
    void atualizar_ShouldSetDate_WhenNull() {
        solicitacao.setDatasolicitacao(null);
        when(solicitacaoRepository.existsById(1L)).thenReturn(true);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao updated = solicitacaoService.atualizar(solicitacao);

        assertNotNull(updated.getDatasolicitacao());
    }

    @Test
    void atualizar_ShouldThrowException_WhenNotExists() {
        when(solicitacaoRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> solicitacaoService.atualizar(solicitacao));
        verify(solicitacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar o status de uma solicitação")
    void setStatus_ShouldChangeStatus() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findById(2L)).thenReturn(Optional.of(statusConcluida));
        when(solicitacaoRepository.saveAndFlush(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao updated = solicitacaoService.setStatus(1L, 2L);

        assertEquals("Concluída", updated.getStatusSolicitacao().getStatus());
        verify(solicitacaoRepository, times(1)).saveAndFlush(solicitacao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar status se a solicitação não for encontrada")
    void setStatus_ShouldThrowException_WhenSolicitacaoNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> solicitacaoService.setStatus(1L, 2L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar status se o status não for encontrado")
    void setStatus_ShouldThrowException_WhenStatusNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> solicitacaoService.setStatus(1L, 2L));
    }

    @Test
    @DisplayName("Deve alterar o status de uma solicitação pelo nome do status")
    void setStatusPorNome_ShouldChangeStatus() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findByStatus("Concluída")).thenReturn(Optional.of(statusConcluida));
        when(solicitacaoRepository.saveAndFlush(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao updated = solicitacaoService.setStatusPorNome(1L, "Concluída");

        assertEquals("Concluída", updated.getStatusSolicitacao().getStatus());
        verify(solicitacaoRepository, times(1)).saveAndFlush(solicitacao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar status por nome se o status não for encontrado")
    void setStatusPorNome_ShouldThrowException_WhenStatusNameNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findByStatus("Inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> solicitacaoService.setStatusPorNome(1L, "Inexistente"));
    }

    @Test
    @DisplayName("Deve concluir uma solicitação, definindo data e adicionando observação")
    void concluir_ShouldSetDateAndAppendObservation() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        String obsConclusao = "Tarefa finalizada.";
        Solicitacao concluida = solicitacaoService.concluir(1L, obsConclusao);

        assertNotNull(concluida.getDataconclusao());
        assertEquals("Observação inicial\n\nConclusão: " + obsConclusao, concluida.getObservacao());
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }

    @Test
    @DisplayName("Deve concluir uma solicitação sem alterar a observação se a nova for nula")
    void concluir_ShouldNotChangeObservation_WhenNewObservationIsNull() {
        solicitacao.setObservacao("Obs original.");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao concluida = solicitacaoService.concluir(1L, null);

        assertNotNull(concluida.getDataconclusao()); // A data de conclusão ainda deve ser definida
        assertEquals("Obs original.", concluida.getObservacao()); // Observation should not change
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }

    @Test
    @DisplayName("Deve concluir uma solicitação sem alterar a observação se a nova for em branco")
    void concluir_ShouldNotChangeObservation_WhenNewObservationIsBlank() {
        solicitacao.setObservacao("Obs original.");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        Solicitacao concluida = solicitacaoService.concluir(1L, "   "); // Observação em branco

        assertNotNull(concluida.getDataconclusao());
        assertEquals("Obs original.", concluida.getObservacao());
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }


    @Test
    void concluir_ShouldThrowException_WhenNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> solicitacaoService.concluir(1L, "obs"));
    }

    @Test
    void deletar_ShouldDeleteSolicitacaoAndRelatedEntities() {
        when(solicitacaoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findBySolicitacaoIdsolicitacao(1L)).thenReturn(new ArrayList<>());
        doNothing().when(soliArquivoRepository).deleteAll(any());
        doNothing().when(solicitacaoRepository).deleteHistoricoBySolicitacaoId(1L);
        doNothing().when(solicitacaoRepository).deleteById(1L);

        solicitacaoService.deletar(1L);

        verify(soliArquivoRepository, times(1)).deleteAll(any());
        verify(solicitacaoRepository, times(1)).deleteHistoricoBySolicitacaoId(1L);
        verify(solicitacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotExists() {
        when(solicitacaoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> solicitacaoService.deletar(1L));
        verify(solicitacaoRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasDTO_ShouldReturnListOfDTOs() {
        when(solicitacaoRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of(solicitacao));
        List<SolicitacaoDTO> dtos = solicitacaoService.listarTodasDTO();
        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getIdsolicitacao());
    }

    @Test
    void listarTodas_ShouldReturnListOfSolicitacoes() {
        when(solicitacaoRepository.findAll(any(Sort.class))).thenReturn(List.of(solicitacao));

        List<Solicitacao> result = solicitacaoService.listarTodas();

        assertEquals(1, result.size());
    }

    @Test
    void listarTodas_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findAll(pageable)).thenReturn(page);

        Page<Solicitacao> result = solicitacaoService.listarTodas(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarTodasDTO_WithPageable_ShouldReturnPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao), pageable, 1);
        when(solicitacaoRepository.findAll(pageable)).thenReturn(page);

        Page<SolicitacaoDTO> dtoPage = solicitacaoService.listarTodasDTO(pageable);

        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1L, dtoPage.getContent().get(0).getIdsolicitacao());
    }

    @Test
    void marcarComoPago_ShouldThrowException_WhenNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> solicitacaoService.marcarComoPago(1L));
    }

    @Test
    void marcarComoPago_ShouldSetPagoToTrue() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenAnswer(i -> i.getArgument(0));

        Solicitacao result = solicitacaoService.marcarComoPago(1L);

        assertEquals("true", result.getPago());
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }

    @Test
    void marcarComoNaoPago_ShouldSetPagoToFalse() {
        solicitacao.setPago("true");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenAnswer(i -> i.getArgument(0));

        Solicitacao result = solicitacaoService.marcarComoNaoPago(1L);

        assertEquals("false", result.getPago());
        verify(solicitacaoRepository, times(1)).save(solicitacao);
    }

    @Test
    void marcarComoNaoPago_ShouldThrowException_WhenNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> solicitacaoService.marcarComoNaoPago(1L));
    }

    @Test
    void buscarPorId_ShouldReturnSolicitacao_WhenFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        Optional<Solicitacao> result = solicitacaoService.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void buscarPorId_ShouldReturnEmpty_WhenNotFound() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Solicitacao> result = solicitacaoService.buscarPorId(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void toDTO_ShouldConvertCorrectly() {
        // Setup all fields
        solicitacao.setProcesso(processo);
        solicitacao.setComarca(comarca);
        solicitacao.setUsuario(usuario);
        tipoSolicitacao = new TipoSolicitacao(1L, "Especie", "Desc", "T", true);
        solicitacao.setTipoSolicitacao(tipoSolicitacao);
        correspondente = new Correspondente();
        correspondente.setId(1L);
        correspondente.setNome("Corresp Test");
        solicitacao.setCorrespondente(correspondente);

        SolicitacaoDTO dto = solicitacaoService.toDTO(solicitacao);

        assertEquals(solicitacao.getId(), dto.getIdsolicitacao());
        assertEquals(processo.getId(), dto.getProcessoId());
        assertEquals(comarca.getId(), dto.getComarcaId());
        assertEquals(usuario.getId(), dto.getUsuarioId());
        assertEquals(tipoSolicitacao.getIdtiposolicitacao(), dto.getTipoSolicitacaoId());
        assertEquals(correspondente.getId(), dto.getCorrespondenteId());
    }

    @Test
    void toDTO_ShouldHandleNullFields() {
        Solicitacao solicitacaoComNulos = new Solicitacao();
        solicitacaoComNulos.setId(2L);

        SolicitacaoDTO dto = solicitacaoService.toDTO(solicitacaoComNulos);

        assertEquals(2L, dto.getIdsolicitacao());
        assertNull(dto.getProcessoId());
        assertNull(dto.getComarcaId());
        assertNull(dto.getUsuarioId());
        assertNull(dto.getTipoSolicitacaoId());
        assertNull(dto.getCorrespondenteId());
    }

    @Test
    void listarTodas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findAll(sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarTodas(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findAll(sort);
    }

    @Test
    void contarTodas_ShouldReturnCount() {
        when(solicitacaoRepository.countAllSolicitacoes()).thenReturn(10L);
        long count = solicitacaoService.contarTodas();
        assertEquals(10L, count);
    }

    @Test
    void buscarPorUsuario_ShouldReturnList() {
        when(solicitacaoRepository.findByUsuario(any(Usuario.class), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorUsuario(usuario);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorUsuario_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByUsuario(usuario, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorUsuario(usuario, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByUsuario(usuario, sort);
    }

    @Test
    void buscarPorUsuario_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorUsuario(usuario, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorProcesso_ShouldReturnList() {
        when(solicitacaoRepository.findByProcesso(any(Processo.class), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorProcesso(processo);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorProcesso_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByProcesso(processo, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorProcesso(processo, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByProcesso(processo, sort);
    }

    @Test
    void buscarPorProcesso_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByProcesso(processo, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorProcesso(processo, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorComarca_ShouldReturnList() {
        when(solicitacaoRepository.findByComarca(any(Comarca.class), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorComarca(comarca);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorComarca_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByComarca(comarca, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorComarca(comarca, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByComarca(comarca, sort);
    }

    @Test
    void buscarPorComarca_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByComarca(comarca, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorComarca(comarca, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorComarcaECorrespondente_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByComarcaAndCorrespondente(comarca, correspondente, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorComarcaECorrespondente(comarca, correspondente, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorCorrespondente_ShouldReturnList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "datasolicitacao");
        when(solicitacaoRepository.findByCorrespondente(correspondente, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(correspondente);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorCorrespondente_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByCorrespondente(correspondente, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(correspondente, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByCorrespondente(correspondente, sort);
    }

    @Test
    void buscarPorCorrespondente_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByCorrespondente(correspondente, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorCorrespondente(correspondente, pageable);
        assertEquals(1, result.getTotalElements());
    }





    @Test
    void buscarPorUsuarioECorrespondente_ShouldReturnList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "datasolicitacao");
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(usuario, correspondente);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorUsuarioECorrespondente_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(usuario, correspondente, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByUsuarioAndCorrespondente(usuario, correspondente, sort);
    }

    @Test
    void buscarPorUsuarioECorrespondente_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByUsuarioAndCorrespondente(usuario, correspondente, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorUsuarioECorrespondente(usuario, correspondente, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorPeriodo_ShouldReturnList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(solicitacaoRepository.findByDatasolicitacaoBetween(any(), any(), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorPeriodo(start, end);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorPeriodo_WithSort_ShouldReturnSortedList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByDatasolicitacaoBetween(start, end, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorPeriodo(start, end, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByDatasolicitacaoBetween(start, end, sort);
    }

    @Test
    void buscarPorPeriodo_WithPageable_ShouldReturnPage() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByDatasolicitacaoBetween(start, end, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorPeriodo(start, end, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarPendentes_ShouldReturnList() {
        when(solicitacaoRepository.findPendentes(any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarPendentes();
        assertEquals(1, result.size());
    }

    @Test
    void listarPendentes_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findPendentes(sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarPendentes(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findPendentes(sort);
    }

    @Test
    void listarPendentes_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findPendentes(pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.listarPendentes(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarConcluidas_ShouldReturnList() {
        when(solicitacaoRepository.findConcluidas(any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarConcluidas();
        assertEquals(1, result.size());
    }

    @Test
    void listarConcluidas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findConcluidas(sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarConcluidas(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findConcluidas(sort);
    }

    @Test
    void listarConcluidas_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findConcluidas(pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.listarConcluidas(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarPagas_ShouldReturnList() {
        when(solicitacaoRepository.findByPagoTrue(any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarPagas();
        assertEquals(1, result.size());
    }

    @Test
    void listarPagas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByPagoTrue(sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarPagas(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByPagoTrue(sort);
    }

    @Test
    void listarPagas_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByPagoTrue(pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.listarPagas(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarNaoPagas_ShouldReturnList() {
        when(solicitacaoRepository.findByPagoFalse(any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarNaoPagas();
        assertEquals(1, result.size());
    }

    @Test
    void listarNaoPagas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByPagoFalse(sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarNaoPagas(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByPagoFalse(sort);
    }

    @Test
    void listarNaoPagas_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByPagoFalse(pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.listarNaoPagas(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listarAtrasadas_ShouldReturnList() {
        when(solicitacaoRepository.findAtrasadas(any(), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarAtrasadas();
        assertEquals(1, result.size());
    }

    @Test
    void listarAtrasadas_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findAtrasadas(any(), eq(sort))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.listarAtrasadas(sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findAtrasadas(any(), eq(sort));
    }

    @Test
    void listarAtrasadas_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findAtrasadas(any(), eq(pageable))).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.listarAtrasadas(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorTexto_ShouldReturnList() {
        when(solicitacaoRepository.findByTextoContaining(anyString(), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorTexto("texto");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorTexto_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByTextoContaining("texto", sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorTexto("texto", sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByTextoContaining("texto", sort);
    }

    @Test
    void buscarPorTexto_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByTextoContaining("texto", pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorTexto("texto", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorGrupo_ShouldReturnList() {
        when(solicitacaoRepository.findByGrupo(anyInt(), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorGrupo(1);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorGrupo_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByGrupo(1, sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorGrupo(1, sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByGrupo(1, sort);
    }

    @Test
    void buscarPorGrupo_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByGrupo(1, pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorGrupo(1, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorStatusExterno_ShouldReturnList() {
        when(solicitacaoRepository.findByStatusexterno(anyString(), any(Sort.class))).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("status");
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorStatusExterno_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("id");
        when(solicitacaoRepository.findByStatusexterno("status", sort)).thenReturn(List.of(solicitacao));
        List<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("status", sort);
        assertEquals(1, result.size());
        verify(solicitacaoRepository).findByStatusexterno("status", sort);
    }

    @Test
    void buscarPorStatusExterno_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));
        when(solicitacaoRepository.findByStatusexterno("status", pageable)).thenReturn(page);
        Page<Solicitacao> result = solicitacaoService.buscarPorStatusExterno("status", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarAvancado_ShouldCallRepositoryWithSpecification() {
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setComarcaId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));

        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, pageable);

        assertEquals(1, result.getTotalElements());
        verify(solicitacaoRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve retornar uma página vazia na busca avançada se o filtro for nulo")
    void buscarAvancado_ShouldReturnEmptyPage_WhenFilterIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        // Quando o filtro é nulo, o serviço deve retornar uma página vazia sem chamar o repositório.

        Page<Solicitacao> result = solicitacaoService.buscarAvancado(null, pageable);

        assertTrue(result.isEmpty());
        verify(solicitacaoRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void contarPorUsuario_ShouldReturnCount() {
        when(solicitacaoRepository.countByUsuario(usuario)).thenReturn(5L);
        long count = solicitacaoService.contarPorUsuario(usuario);
        assertEquals(5L, count);
    }

    @Test
    void contarPendentes_ShouldReturnCount() {
        when(solicitacaoRepository.countPendentes()).thenReturn(3L);
        long count = solicitacaoService.contarPendentes();
        assertEquals(3L, count);
    }
}