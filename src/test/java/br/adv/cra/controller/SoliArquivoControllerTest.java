package br.adv.cra.controller;

import br.adv.cra.dto.SoliArquivoDTO;
import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.service.SoliArquivoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SoliArquivoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SoliArquivoService soliArquivoService;

    @InjectMocks
    private SoliArquivoController soliArquivoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(soliArquivoController).build();
    }

    @Test
    void testUploadAnexo() throws Exception {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        Long solicitacaoId = 1L;
        String origem = "usuario";

        SoliArquivoDTO dto = new SoliArquivoDTO();
        dto.setId(1L);
        dto.setIdSolicitacao(solicitacaoId);
        dto.setNomearquivo("test.txt");
        dto.setDataInclusao(LocalDateTime.now());
        dto.setOrigem(origem);
        dto.setAtivo(true);
        dto.setCaminhoRelativo("/arquivos/test.txt");

        // Configure mocks
        when(soliArquivoService.salvarAnexo(any(), eq(solicitacaoId), eq(origem))).thenReturn(new SoliArquivo());

        // Execute the request
        mockMvc.perform(multipart("/api/soli-arquivos/upload")
                .file(file)
                .param("solicitacaoId", solicitacaoId.toString())
                .param("origem", origem))
                .andExpect(status().isCreated());

        // Verify interactions
        verify(soliArquivoService, times(1)).salvarAnexo(any(), eq(solicitacaoId), eq(origem));
    }

    @Test
    void testListarAnexosPorSolicitacao() throws Exception {
        // Prepare test data
        Long solicitacaoId = 1L;

        SoliArquivoDTO dto1 = new SoliArquivoDTO();
        dto1.setId(1L);
        dto1.setIdSolicitacao(solicitacaoId);
        dto1.setNomearquivo("test1.txt");
        dto1.setDataInclusao(LocalDateTime.now());
        dto1.setOrigem("usuario");
        dto1.setAtivo(true);
        dto1.setCaminhoRelativo("/arquivos/test1.txt");

        SoliArquivoDTO dto2 = new SoliArquivoDTO();
        dto2.setId(2L);
        dto2.setIdSolicitacao(solicitacaoId);
        dto2.setNomearquivo("test2.txt");
        dto2.setDataInclusao(LocalDateTime.now());
        dto2.setOrigem("correspondente");
        dto2.setAtivo(true);
        dto2.setCaminhoRelativo("/arquivos/test2.txt");

        // Configure mocks
        when(soliArquivoService.listarAnexosPorSolicitacao(solicitacaoId))
                .thenReturn(Arrays.asList(new SoliArquivo(), new SoliArquivo()));

        // Execute the request
        mockMvc.perform(get("/api/soli-arquivos/solicitacao/{solicitacaoId}", solicitacaoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Verify interactions
        verify(soliArquivoService, times(1)).listarAnexosPorSolicitacao(solicitacaoId);
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Prepare test data
        Long id = 1L;

        SoliArquivoDTO dto = new SoliArquivoDTO();
        dto.setId(id);
        dto.setIdSolicitacao(1L);
        dto.setNomearquivo("test.txt");
        dto.setDataInclusao(LocalDateTime.now());
        dto.setOrigem("usuario");
        dto.setAtivo(true);
        dto.setCaminhoRelativo("/arquivos/test.txt");

        // Configure mocks
        when(soliArquivoService.buscarPorId(id)).thenReturn(Optional.of(new SoliArquivo()));

        // Execute the request
        mockMvc.perform(get("/api/soli-arquivos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify interactions
        verify(soliArquivoService, times(1)).buscarPorId(id);
    }

    @Test
    void testBuscarPorIdNotFound() throws Exception {
        // Prepare test data
        Long id = 1L;

        // Configure mocks
        when(soliArquivoService.buscarPorId(id)).thenReturn(Optional.empty());

        // Execute the request
        mockMvc.perform(get("/api/soli-arquivos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(soliArquivoService, times(1)).buscarPorId(id);
    }

    @Test
    void testAtualizar() throws Exception {
        // Prepare test data
        Long id = 1L;

        SoliArquivoDTO dto = new SoliArquivoDTO();
        dto.setId(id);
        dto.setIdSolicitacao(1L);
        dto.setNomearquivo("updated.txt");
        dto.setDataInclusao(LocalDateTime.now());
        dto.setOrigem("usuario");
        dto.setAtivo(true);
        dto.setCaminhoRelativo("/arquivos/updated.txt");

        // Configure mocks
        when(soliArquivoService.atualizar(eq(id), any(SoliArquivo.class))).thenReturn(new SoliArquivo());

        // Execute the request
        mockMvc.perform(put("/api/soli-arquivos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"idSolicitacao\":1,\"nomearquivo\":\"updated.txt\",\"origem\":\"usuario\",\"ativo\":true,\"caminhoRelativo\":\"/arquivos/updated.txt\"}"))
                .andExpect(status().isOk());

        // Verify interactions
        verify(soliArquivoService, times(1)).atualizar(eq(id), any(SoliArquivo.class));
    }

    @Test
    void testDeletar() throws Exception {
        // Prepare test data
        Long id = 1L;
        String origem = "usuario";

        // Configure mocks
        when(soliArquivoService.podeDeletar(id, origem)).thenReturn(true);
        doNothing().when(soliArquivoService).deletar(id);

        // Execute the request
        mockMvc.perform(delete("/api/soli-arquivos/{id}", id)
                .param("origem", origem))
                .andExpect(status().isNoContent());

        // Verify interactions
        verify(soliArquivoService, times(1)).podeDeletar(id, origem);
        verify(soliArquivoService, times(1)).deletar(id);
    }

    @Test
    void testDeletarForbidden() throws Exception {
        // Prepare test data
        Long id = 1L;
        String origem = "correspondente";

        // Configure mocks
        when(soliArquivoService.podeDeletar(id, origem)).thenReturn(false);

        // Execute the request
        mockMvc.perform(delete("/api/soli-arquivos/{id}", id)
                .param("origem", origem))
                .andExpect(status().isForbidden());

        // Verify interactions
        verify(soliArquivoService, times(1)).podeDeletar(id, origem);
        verify(soliArquivoService, never()).deletar(id);
    }
}