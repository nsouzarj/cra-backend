package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import br.adv.cra.specification.SolicitacaoSpecification;
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

class SolicitacaoServiceUnitTest {

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
    void testBuscarAvancadoWithISODateFilter() {
        // Create test dates (simulating ISO format dates from frontend)
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0); // 2025-09-16T00:00:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);   // 2025-09-19T00:00:00

        // Create filtro DTO
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Mock repository response
        Page<Solicitacao> mockPage = new PageImpl<>(Collections.emptyList());
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Call the service method
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verify the result
        assertEquals(mockPage, result);

        // Verify that the repository method was called
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testDataSpecificationCreation() {
        // Test that the specification is created correctly with ISO dates
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);

        // Create specification directly
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, endDate);

        // Verify the specification is not null
        assertEquals(false, spec == null);
    }
}