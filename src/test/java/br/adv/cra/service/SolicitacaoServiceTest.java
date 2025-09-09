package br.adv.cra.service;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private StatusSolicitacaoRepository statusSolicitacaoRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetStatus_Success() {
        // Arrange
        Long solicitacaoId = 1L;
        Long statusId = 1L;
        
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);
        
        StatusSolicitacao status = new StatusSolicitacao();
        status.setIdstatus(statusId);
        status.setStatus("Pendente");
        
        // Criar uma nova instância de solicitação com o status definido para simular o saveAndFlush
        Solicitacao savedSolicitacao = new Solicitacao();
        savedSolicitacao.setId(solicitacaoId);
        savedSolicitacao.setStatusSolicitacao(status);
        
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(solicitacaoRepository.saveAndFlush(solicitacao)).thenReturn(savedSolicitacao);
        
        // Act
        Solicitacao result = solicitacaoService.setStatus(solicitacaoId, statusId);
        
        // Assert
        assertNotNull(result);
        assertEquals(status, result.getStatusSolicitacao());
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(statusSolicitacaoRepository, times(1)).findById(statusId);
        verify(solicitacaoRepository, times(1)).saveAndFlush(solicitacao);
    }

    @Test
    void testSetStatus_SolicitacaoNotFound() {
        // Arrange
        Long solicitacaoId = 1L;
        Long statusId = 1L;
        
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatus(solicitacaoId, statusId);
        });
        
        assertEquals("Solicitação não encontrada", exception.getMessage());
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(statusSolicitacaoRepository, never()).findById(statusId);
        verify(solicitacaoRepository, never()).save(any());
    }

    @Test
    void testSetStatus_StatusNotFound() {
        // Arrange
        Long solicitacaoId = 1L;
        Long statusId = 1L;
        
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);
        
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findById(statusId)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatus(solicitacaoId, statusId);
        });
        
        assertEquals("Status não encontrado", exception.getMessage());
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(statusSolicitacaoRepository, times(1)).findById(statusId);
        verify(solicitacaoRepository, never()).save(any());
    }
    
    @Test
    void testSetStatusPorNome_Success() {
        // Arrange
        Long solicitacaoId = 1L;
        String statusNome = "Pendente";
        
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);
        
        StatusSolicitacao status = new StatusSolicitacao();
        status.setIdstatus(1L);
        status.setStatus(statusNome);
        
        // Criar uma nova instância de solicitação com o status definido para simular o saveAndFlush
        Solicitacao savedSolicitacao = new Solicitacao();
        savedSolicitacao.setId(solicitacaoId);
        savedSolicitacao.setStatusSolicitacao(status);
        
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findByStatus(statusNome)).thenReturn(Optional.of(status));
        when(solicitacaoRepository.saveAndFlush(solicitacao)).thenReturn(savedSolicitacao);
        
        // Act
        Solicitacao result = solicitacaoService.setStatusPorNome(solicitacaoId, statusNome);
        
        // Assert
        assertNotNull(result);
        assertEquals(status, result.getStatusSolicitacao());
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(statusSolicitacaoRepository, times(1)).findByStatus(statusNome);
        verify(solicitacaoRepository, times(1)).saveAndFlush(solicitacao);
    }
    
    @Test
    void testSetStatusPorNome_StatusNotFound() {
        // Arrange
        Long solicitacaoId = 1L;
        String statusNome = "Inexistente";
        
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);
        
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(statusSolicitacaoRepository.findByStatus(statusNome)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.setStatusPorNome(solicitacaoId, statusNome);
        });
        
        assertEquals("Status não encontrado", exception.getMessage());
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(statusSolicitacaoRepository, times(1)).findByStatus(statusNome);
        verify(solicitacaoRepository, never()).save(any());
    }
}