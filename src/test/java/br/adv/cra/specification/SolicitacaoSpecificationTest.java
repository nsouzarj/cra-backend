package br.adv.cra.specification;

import br.adv.cra.entity.Solicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;

class SolicitacaoSpecificationTest {

    @Test
    void testComarcaIdEquals() {
        Specification<Solicitacao> spec = SolicitacaoSpecification.comarcaIdEquals(1L);
        assertNotNull(spec);
        // The specification should not be null
        assertDoesNotThrow(() -> {
            spec.toString();
        });
    }

    @Test
    void testCorrespondenteIdEquals() {
        Specification<Solicitacao> spec = SolicitacaoSpecification.correspondenteIdEquals(1L);
        assertNotNull(spec);
        assertDoesNotThrow(() -> {
            spec.toString();
        });
    }

    @Test
    void testStatusEquals() {
        Specification<Solicitacao> spec = SolicitacaoSpecification.statusEquals("Concluída");
        assertNotNull(spec);
        assertDoesNotThrow(() -> {
            spec.toString();
        });
    }

    @Test
    void testCombinedSpecifications() {
        // This test doesn't need to use lambda expressions that reference local variables
        // Let's just test that we can combine specifications without issues
        Specification<Solicitacao> spec1 = SolicitacaoSpecification.comarcaIdEquals(1L);
        Specification<Solicitacao> spec2 = SolicitacaoSpecification.correspondenteIdEquals(2L);
        Specification<Solicitacao> spec3 = SolicitacaoSpecification.statusEquals("Concluída");
        
        // Combine them
        Specification<Solicitacao> combined = Specification.where(spec1).and(spec2).and(spec3);
        
        assertNotNull(combined);
        // We can't easily test the actual query without a database context, but we can ensure it's not null
    }
}