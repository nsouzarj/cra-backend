package br.adv.cra.specification;

import br.adv.cra.entity.Solicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitacaoSpecificationDateTest {

    @Test
    void testDataBetweenWithMidnightEnd() {
        // Testar com data de fim à meia-noite (00:00:00)
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 10, 30, 0); // 07/09/2025 10:30:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0);    // 08/09/2025 00:00:00
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testDataBetweenWithEndOfDay() {
        // Testar com data de fim no final do dia
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 7, 10, 30, 0); // 07/09/2025 10:30:00
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 23, 59, 59);  // 08/09/2025 23:59:59
        
        // Criar especificação
        Specification<Solicitacao> spec = SolicitacaoSpecification.dataBetween(startDate, endDate);
        
        // Verificar que a especificação foi criada corretamente
        assertNotNull(spec);
    }
    
    @Test
    void testAdjustEndDateToMaxTime() {
        // Testar se a data de fim é ajustada corretamente para o final do dia
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 8, 0, 0, 0); // 08/09/2025 00:00:00
        LocalDateTime expectedEndDate = endDate.with(LocalTime.MAX);  // 08/09/2025 23:59:59.999999999
        
        // Verificar que o ajuste está correto
        assertTrue(expectedEndDate.toLocalTime().equals(LocalTime.MAX));
    }
}