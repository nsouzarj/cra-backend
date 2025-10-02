package br.adv.cra.service;

import br.adv.cra.dto.StatusSolicitacaoDTO;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.repository.StatusSolicitacaoRepository;
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

class StatusSolicitacaoServiceTest {

    @Mock
    private StatusSolicitacaoRepository statusSolicitacaoRepository;

    private StatusSolicitacaoService statusSolicitacaoService;

    private StatusSolicitacao testStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statusSolicitacaoService = new StatusSolicitacaoService(statusSolicitacaoRepository);

        // Create test status
        testStatus = new StatusSolicitacao();
        testStatus.setIdstatus(1L);
        testStatus.setStatus("Pendente");
    }

    @Test
    void testSalvar_ShouldSaveStatusSolicitacao() {
        // Given
        StatusSolicitacao statusToSave = new StatusSolicitacao();
        statusToSave.setStatus("Concluído");

        when(statusSolicitacaoRepository.save(any(StatusSolicitacao.class))).thenReturn(testStatus);

        // When
        StatusSolicitacao result = statusSolicitacaoService.salvar(statusToSave);

        // Then
        assertNotNull(result);
        assertEquals(testStatus, result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).save(statusToSave);
    }

    @Test
    void testAtualizar_ExistingStatus_ShouldUpdateStatus() {
        // Given
        StatusSolicitacao statusToUpdate = new StatusSolicitacao();
        statusToUpdate.setIdstatus(1L);
        statusToUpdate.setStatus("Atualizado");

        when(statusSolicitacaoRepository.existsById(1L)).thenReturn(true);
        when(statusSolicitacaoRepository.save(any(StatusSolicitacao.class))).thenReturn(statusToUpdate);

        // When
        StatusSolicitacao result = statusSolicitacaoService.atualizar(statusToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Atualizado", result.getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).existsById(1L);
        verify(statusSolicitacaoRepository, times(1)).save(statusToUpdate);
    }

    @Test
    void testAtualizar_NonExistentStatus_ShouldThrowException() {
        // Given
        StatusSolicitacao statusToUpdate = new StatusSolicitacao();
        statusToUpdate.setIdstatus(999L);
        statusToUpdate.setStatus("Non-existent");

        when(statusSolicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            statusSolicitacaoService.atualizar(statusToUpdate);
        });

        assertEquals("Status de solicitação não encontrado", exception.getMessage());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).existsById(999L);
        verify(statusSolicitacaoRepository, never()).save(any(StatusSolicitacao.class));
    }

    @Test
    void testDeletar_ExistingStatus_ShouldDeleteStatus() {
        // Given
        when(statusSolicitacaoRepository.existsById(1L)).thenReturn(true);

        // When
        statusSolicitacaoService.deletar(1L);

        // Then
        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).existsById(1L);
        verify(statusSolicitacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentStatus_ShouldThrowException() {
        // Given
        when(statusSolicitacaoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            statusSolicitacaoService.deletar(999L);
        });

        assertEquals("Status de solicitação não encontrado", exception.getMessage());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).existsById(999L);
        verify(statusSolicitacaoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingStatus_ShouldReturnStatus() {
        // Given
        when(statusSolicitacaoRepository.findById(1L)).thenReturn(Optional.of(testStatus));

        // When
        Optional<StatusSolicitacao> result = statusSolicitacaoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testStatus, result.get());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentStatus_ShouldReturnEmpty() {
        // Given
        when(statusSolicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<StatusSolicitacao> result = statusSolicitacaoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findById(999L);
    }

    @Test
    void testBuscarPorStatus_ExistingStatus_ShouldReturnStatus() {
        // Given
        when(statusSolicitacaoRepository.findByStatus("Pendente")).thenReturn(Optional.of(testStatus));

        // When
        Optional<StatusSolicitacao> result = statusSolicitacaoService.buscarPorStatus("Pendente");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testStatus, result.get());
        assertEquals("Pendente", result.get().getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatus("Pendente");
    }

    @Test
    void testBuscarPorStatus_NonExistentStatus_ShouldReturnEmpty() {
        // Given
        when(statusSolicitacaoRepository.findByStatus("NonExistent")).thenReturn(Optional.empty());

        // When
        Optional<StatusSolicitacao> result = statusSolicitacaoService.buscarPorStatus("NonExistent");

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatus("NonExistent");
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllStatusAsDTOs() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L);
        status2.setStatus("Concluído");

        when(statusSolicitacaoRepository.findAllOrderByStatus()).thenReturn(Arrays.asList(status1, status2));

        // When
        List<StatusSolicitacaoDTO> result = statusSolicitacaoService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pendente", result.get(0).getStatus());
        assertEquals("Concluído", result.get(1).getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findAllOrderByStatus();
    }

    @Test
    void testListarTodos_ShouldReturnAllStatusOrderedByStatus() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L);
        status2.setStatus("Concluído");

        when(statusSolicitacaoRepository.findAllOrderByStatus()).thenReturn(Arrays.asList(status1, status2));

        // When
        List<StatusSolicitacao> result = statusSolicitacaoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pendente", result.get(0).getStatus());
        assertEquals("Concluído", result.get(1).getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findAllOrderByStatus();
    }

    @Test
    void testListarTodosWithSort_ShouldReturnAllStatusWithCustomSorting() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L);
        status2.setStatus("Concluído");

        Sort sort = Sort.by(Sort.Direction.DESC, "status");

        when(statusSolicitacaoRepository.findAll(sort)).thenReturn(Arrays.asList(status2, status1));

        // When
        List<StatusSolicitacao> result = statusSolicitacaoService.listarTodos(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Concluído", result.get(0).getStatus());
        assertEquals("Pendente", result.get(1).getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findAll(sort);
    }

    @Test
    void testBuscarPorStatusContaining_ShouldReturnMatchingStatus() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L);
        status2.setStatus("Parcialmente Pendente");

        when(statusSolicitacaoRepository.findByStatusContaining("Pendente")).thenReturn(Arrays.asList(status1, status2));

        // When
        List<StatusSolicitacao> result = statusSolicitacaoService.buscarPorStatusContaining("Pendente");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getStatus().contains("Pendente"));
        assertTrue(result.get(1).getStatus().contains("Pendente"));

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("Pendente");
    }

    @Test
    void testBuscarPorStatusContainingWithSort_ShouldReturnMatchingStatusWithCustomSorting() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L);
        status2.setStatus("Parcialmente Pendente");

        Sort sort = Sort.by(Sort.Direction.DESC, "status");

        when(statusSolicitacaoRepository.findByStatusContaining(eq("Pendente"), any(Sort.class))).thenReturn(Arrays.asList(status2, status1));

        // When
        List<StatusSolicitacao> result = statusSolicitacaoService.buscarPorStatusContaining("Pendente", sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Parcialmente Pendente", result.get(0).getStatus());
        assertEquals("Pendente", result.get(1).getStatus());

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining(eq("Pendente"), any(Sort.class));
    }

    @Test
    void testExisteStatus_ExistingStatus_ShouldReturnTrue() {
        // Given
        StatusSolicitacao status = new StatusSolicitacao();
        status.setIdstatus(1L);
        status.setStatus("Pendente");

        when(statusSolicitacaoRepository.findByStatusContaining("Pendente")).thenReturn(Arrays.asList(status));

        // When
        boolean result = statusSolicitacaoService.existeStatus("Pendente");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("Pendente");
    }

    @Test
    void testExisteStatus_NonExistentStatus_ShouldReturnFalse() {
        // Given
        when(statusSolicitacaoRepository.findByStatusContaining("NonExistent")).thenReturn(Arrays.asList());

        // When
        boolean result = statusSolicitacaoService.existeStatus("NonExistent");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("NonExistent");
    }

    @Test
    void testExisteStatusParaOutroStatus_ExistingStatusForDifferentStatus_ShouldReturnTrue() {
        // Given
        StatusSolicitacao status1 = new StatusSolicitacao();
        status1.setIdstatus(1L);
        status1.setStatus("Pendente");

        StatusSolicitacao status2 = new StatusSolicitacao();
        status2.setIdstatus(2L); // Different ID
        status2.setStatus("Pendente");

        when(statusSolicitacaoRepository.findByStatusContaining("Pendente")).thenReturn(Arrays.asList(status1, status2));

        // When
        boolean result = statusSolicitacaoService.existeStatusParaOutroStatus("Pendente", 1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("Pendente");
    }

    @Test
    void testExisteStatusParaOutroStatus_ExistingStatusForSameStatus_ShouldReturnFalse() {
        // Given
        StatusSolicitacao status = new StatusSolicitacao();
        status.setIdstatus(1L); // Same ID
        status.setStatus("Pendente");

        when(statusSolicitacaoRepository.findByStatusContaining("Pendente")).thenReturn(Arrays.asList(status));

        // When
        boolean result = statusSolicitacaoService.existeStatusParaOutroStatus("Pendente", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("Pendente");
    }

    @Test
    void testExisteStatusParaOutroStatus_NonExistentStatus_ShouldReturnFalse() {
        // Given
        when(statusSolicitacaoRepository.findByStatusContaining("NonExistent")).thenReturn(Arrays.asList());

        // When
        boolean result = statusSolicitacaoService.existeStatusParaOutroStatus("NonExistent", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(statusSolicitacaoRepository, times(1)).findByStatusContaining("NonExistent");
    }
}