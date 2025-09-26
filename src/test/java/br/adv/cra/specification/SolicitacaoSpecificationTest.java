package br.adv.cra.specification;

import br.adv.cra.entity.Solicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SolicitacaoSpecificationTest {

    @Test
    void testDataBetweenSpecification() {
        // Create test dates
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);
        
        // Create specification
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, endDate);
        
        // Verify the specification is created correctly
        assertNotNull(spec);
    }
    
    @Test
    void testDataConclusaoBetweenSpecification() {
        // Create test dates
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);
        
        // Create specification
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataConclusaoBetween(startDate, endDate);
        
        // Verify the specification is created correctly
        assertNotNull(spec);
    }
    
    @Test
    void testDataPrazoBetweenSpecification() {
        // Create test dates
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 16, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 19, 0, 0, 0);
        
        // Create specification
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataPrazoBetween(startDate, endDate);
        
        // Verify the specification is created correctly
        assertNotNull(spec);
    }
}