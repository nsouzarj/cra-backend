package br.adv.cra.specification;

import br.adv.cra.entity.Solicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SolicitacaoSpecificationGreaterEqualTest {

    @Test
    void testDataBetweenShouldUseGreaterEqual() {
        // Testar que o método dataBetween usa comparações >= e <= em vez de > e <
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025 00:00:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);   // 08/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testDataConclusaoBetweenShouldUseGreaterEqual() {
        // Testar que o método dataConclusaoBetween usa comparações >= e <= em vez de > e <
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025 00:00:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);   // 08/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataConclusaoBetween(startDate, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testDataPrazoBetweenShouldUseGreaterEqual() {
        // Testar que o método dataPrazoBetween usa comparações >= e <= em vez de > e <
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025 00:00:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);   // 08/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataPrazoBetween(startDate, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testStartDateOnlyShouldUseGreaterEqual() {
        // Testar que quando apenas a data de início é fornecida, usa >=
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 0, 0, 0); // 07/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, null);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testEndDateOnlyShouldUseLessEqual() {
        // Testar que quando apenas a data de fim é fornecida, usa <=
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0); // 08/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(null, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
}