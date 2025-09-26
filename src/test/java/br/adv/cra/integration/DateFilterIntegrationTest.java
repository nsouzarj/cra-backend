package br.adv.cra.integration;

import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SolicitacaoRepository;
import br.adv.cra.service.SolicitacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DateFilterIntegrationTest {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Test
    void testDateFilteringWithISOFormat() {
        // Create test data
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);

        // Create filtro DTO with ISO format dates
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setDataInicio(startDate);
        filtro.setDataFim(endDate);
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setSortBy("id");
        filtro.setDirection("ASC");

        // Call the service method
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, PageRequest.of(0, 10, Sort.by("id")));

        // Verify the result
        assertNotNull(result);
    }
}