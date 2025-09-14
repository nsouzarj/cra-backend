package br.adv.cra.service;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.SolicitacaoAnexo;
import br.adv.cra.entity.Usuario;
import br.adv.cra.repository.SolicitacaoAnexoRepository;
import br.adv.cra.repository.SolicitacaoPossuiArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoAnexoServiceTest {

    @Mock
    private SolicitacaoAnexoRepository solicitacaoAnexoRepository;

    @Mock
    private SolicitacaoPossuiArquivoRepository solicitacaoPossuiArquivoRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @InjectMocks
    private SolicitacaoAnexoService solicitacaoAnexoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the upload directory for testing
        solicitacaoAnexoService.setUploadDir(System.getProperty("java.io.tmpdir"));
    }

    @Test
    void testSalvarAnexo() throws IOException {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        Long solicitacaoId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);

        SolicitacaoAnexo anexo = new SolicitacaoAnexo();
        anexo.setIdarquivoanexo(1L);
        anexo.setNomearquivo("test.txt");
        anexo.setTipoarquivo("text/plain");
        anexo.setDatasolicitacao(LocalDateTime.now());
        anexo.setUsuario(usuario);

        // Configure mocks
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoAnexoRepository.save(any(SolicitacaoAnexo.class))).thenReturn(anexo);

        // Execute the method
        SolicitacaoAnexo result = solicitacaoAnexoService.salvarAnexo(file, solicitacaoId, usuario);

        // Verify results
        assertNotNull(result);
        assertEquals("test.txt", result.getNomearquivo());
        assertEquals("text/plain", result.getTipoarquivo());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(solicitacaoAnexoRepository, times(1)).save(any(SolicitacaoAnexo.class));
        verify(solicitacaoPossuiArquivoRepository, times(1)).save(any());
    }

    @Test
    void testSalvarAnexoSolicitacaoNotFound() {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        Long solicitacaoId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Configure mocks
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.empty());

        // Execute the method and verify exception
        assertThrows(RuntimeException.class, () -> {
            solicitacaoAnexoService.salvarAnexo(file, solicitacaoId, usuario);
        });

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(solicitacaoAnexoRepository, never()).save(any(SolicitacaoAnexo.class));
        verify(solicitacaoPossuiArquivoRepository, never()).save(any());
    }
}