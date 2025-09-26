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

class SolicitacaoServiceExactDateTest {

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
    void testBuscarAvancadoWithExactSameDate() {
        // Testar o cenário: data >= 12/12/2023 AND data <= 12/12/2023
        // Deve trazer registros com data exatamente igual a 12/12/2023
        
        LocalDateTime sameDate = LocalDateTime.of(2023, 12, 12, 0, 0, 0);
        
        // Criar filtro DTO com a mesma data para início e fim
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(sameDate);
        filtro.setDataFim(sameDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Criar solicitações de exemplo com a data exata
        Solicitacao solicitacao1 = new Solicitacao();
        solicitacao1.setId(1L);
        solicitacao1.setDatasolicitacao(LocalDateTime.of(2023, 12, 12, 10, 30, 0)); // 12/12/2023 10:30:00

        Solicitacao solicitacao2 = new Solicitacao();
        solicitacao2.setId(2L);
        solicitacao2.setDatasolicitacao(LocalDateTime.of(2023, 12, 12, 14, 15, 30)); // 12/12/2023 14:15:30

        List<Solicitacao> solicitacoesList = Arrays.asList(solicitacao1, solicitacao2);
        Page<Solicitacao> mockPage = new PageImpl<>(solicitacoesList);

        // Mock da resposta do repositório
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado - deve trazer os dois registros
        assertEquals(2, result.getTotalElements());
        assertEquals(solicitacao1.getId(), result.getContent().get(0).getId());
        assertEquals(solicitacao2.getId(), result.getContent().get(1).getId());

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    void testBuscarAvancadoWithExactDateRange() {
        // Testar o cenário: data >= 12/12/2023 AND data <= 15/12/2023
        // Deve trazer registros nesse intervalo
        
        LocalDateTime startDate = LocalDateTime.of(2023, 12, 12, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 15, 0, 0, 0);
        
        // Criar filtro DTO
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Criar solicitações de exemplo no intervalo
        Solicitacao solicitacao1 = new Solicitacao();
        solicitacao1.setId(1L);
        solicitacao1.setDatasolicitacao(LocalDateTime.of(2023, 12, 12, 10, 30, 0)); // 12/12/2023 10:30:00

        Solicitacao solicitacao2 = new Solicitacao();
        solicitacao2.setId(2L);
        solicitacao2.setDatasolicitacao(LocalDateTime.of(2023, 12, 14, 14, 15, 30)); // 14/12/2023 14:15:30
        
        Solicitacao solicitacao3 = new Solicitacao();
        solicitacao3.setId(3L);
        solicitacao3.setDatasolicitacao(LocalDateTime.of(2023, 12, 15, 9, 0, 0)); // 15/12/2023 09:00:00

        List<Solicitacao> solicitacoesList = Arrays.asList(solicitacao1, solicitacao2, solicitacao3);
        Page<Solicitacao> mockPage = new PageImpl<>(solicitacoesList);

        // Mock da resposta do repositório
        when(solicitacaoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Chamar o método do serviço
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10));

        // Verificar o resultado - deve trazer os três registros
        assertEquals(3, result.getTotalElements());
        assertEquals(solicitacao1.getId(), result.getContent().get(0).getId());
        assertEquals(solicitacao2.getId(), result.getContent().get(1).getId());
        assertEquals(solicitacao3.getId(), result.getContent().get(2).getId());

        // Verificar que o método do repositório foi chamado
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}