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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoServiceDateRangeTest {

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
    void testBuscarAvancadoWithExactDateRangeIssue() {
        // Testar o cenário específico mencionado: datas de 07/09/2025 e 08/09/2025
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025 00:00:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);   // 08/09/2025 00:00:00

        // Criar filtro DTO
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Criar solicitações de exemplo que devem ser retornadas
        Solicitacao solicitacao1 = new Solicitacao();
        solicitacao1.setId(1L);
        solicitacao1.setDatasolicitacao(LocalDateTime.of(2025, 9, 7, 10, 30, 0)); // 07/09/2025 10:30:00

        Solicitacao solicitacao2 = new Solicitacao();
        solicitacao2.setId(2L);
        solicitacao2.setDatasolicitacao(LocalDateTime.of(2025, 9, 8, 14, 15, 30)); // 08/09/2025 14:15:30

        List<Solicitacao> solicitacoesList = Arrays.asList(solicitacao1, solicitacao2);
        Page<Solicitacao> mockPage = new PageImpl<>(solicitacoesList);

        // Mock da resposta do repositório
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado
        assertEquals(2, result.getTotalElements());
        assertEquals(solicitacao1.getId(), result.getContent().get(0).getId());
        assertEquals(solicitacao2.getId(), result.getContent().get(1).getId());

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testBuscarAvancadoWithDateTimeRange() {
        // Testar com horários específicos
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

        // Criar solicitação de exemplo que deve ser retornada
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(1L);
        solicitacao.setDatasolicitacao(LocalDateTime.of(2025, 9, 7, 14, 20, 15)); // 07/09/2025 14:20:15

        List<Solicitacao> solicitacoesList = Arrays.asList(solicitacao);
        Page<Solicitacao> mockPage = new PageImpl<>(solicitacoesList);

        // Mock da resposta do repositório
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado
        assertEquals(1, result.getTotalElements());
        assertEquals(solicitacao.getId(), result.getContent().get(0).getId());

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}