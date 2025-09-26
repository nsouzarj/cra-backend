package br.adv.cra.service;

import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SolicitacaoServiceFullDateTest {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Test
    void testBuscarAvancadoWithISODateFilter() {
        // Create test data
        Solicitacao solicitacao1 = new Solicitacao();
        solicitacao1.setDatasolicitacao(LocalDateTime.of(2025, 9, 15, 10, 0, 0));
        
        Solicitacao solicitacao2 = new Solicitacao();
        solicitacao2.setDatasolicitacao(LocalDateTime.of(2025, 9, 17, 10, 0, 0));
        
        Solicitacao solicitacao3 = new Solicitacao();
        solicitacao3.setDatasolicitacao(LocalDateTime.of(2025, 9, 20, 10, 0, 0));
        
        // Save test data
        solicitacaoRepository.save(solicitacao1);
        solicitacaoRepository.save(solicitacao2);
        solicitacaoRepository.save(solicitacao3);
        solicitacaoRepository.flush();
        
        // Create filtro DTO with ISO format dates (simulating what comes from frontend)
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(LocalDateTime.of(2025, 9, 16, 0, 0, 0)); // 2025-09-16T00:00:00
        filtro.setDataFim(LocalDateTime.of(2025, 9, 19, 0, 0, 0));   // 2025-09-19T00:00:00
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");
        
        // Call the service method
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10, Sort.by("id")));
        
        // Should find only solicitacao2
        assertEquals(1, result.getTotalElements());
        assertNotNull(result.getContent().get(0));
    }
}