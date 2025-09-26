package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoServiceDateFilterTest {

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
    void testBuscarAvancadoWithExactDateRange() {
        // Criar datas exatas como mencionado no problema
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);   // 08/09/2025

        // Criar filtro DTO
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Mock da resposta do repositório
        Page<Solicitacao> mockPage = new PageImpl<>(Collections.emptyList());
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado
        assertEquals(mockPage, result);

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    void testBuscarAvancadoWithDateTimeRange() {
        // Criar datas com horários específicos
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 10, 30, 0); // 07/09/2025 10:30:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 15, 45, 30);   // 08/09/2025 15:45:30

        // Criar filtro DTO
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Mock da resposta do repositório
        Page<Solicitacao> mockPage = new PageImpl<>(Collections.emptyList());
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado
        assertEquals(mockPage, result);

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}