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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitacaoServiceCombinedFilterTest {

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
    @SuppressWarnings("unchecked")
    void testBuscarAvancado_CombinedFilters() {
        // Arrange
        SolicitacaoFiltroDTO filtro = new SolicitacaoFiltroDTO();
        filtro.setComarcaId(1L);
        filtro.setCorrespondenteId(2L);
        filtro.setStatus("Concluída");
        
        Pageable pageable = PageRequest.of(0, 10);
        List<Solicitacao> solicitacoes = Arrays.asList(new Solicitacao());
        Page<Solicitacao> page = new PageImpl<>(solicitacoes);
        
        when(solicitacaoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        
        // Act
        Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(solicitacaoRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}