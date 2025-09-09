package br.adv.cra.controller;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import br.adv.cra.service.ComarcaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

// Completely standalone test without Spring context
class ComarcaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ComarcaService comarcaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Comarca comarca1;
    private Comarca comarca2;
    private Uf uf;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Manual setup without Spring context
        ComarcaController comarcaController = new ComarcaController(comarcaService);
        mockMvc = standaloneSetup(comarcaController).build();
        
        // Create test data
        uf = new Uf(1L, "SP", "São Paulo");
        comarca1 = new Comarca();
        comarca1.setId(1L);
        comarca1.setNome("São Paulo");
        comarca1.setUf(uf);
        
        comarca2 = new Comarca();
        comarca2.setId(2L);
        comarca2.setNome("Campinas");
        comarca2.setUf(uf);
    }

    @Test
    void testCriar() throws Exception {
        when(comarcaService.salvar(comarca1)).thenReturn(comarca1);

        mockMvc.perform(post("/api/comarcas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comarca1)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("São Paulo"));
    }

    @Test
    void testAtualizar_Success() throws Exception {
        when(comarcaService.buscarPorId(1L)).thenReturn(Optional.of(comarca1));
        when(comarcaService.atualizar(comarca1)).thenReturn(comarca1);

        mockMvc.perform(put("/api/comarcas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comarca1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("São Paulo"));
    }

    @Test
    void testAtualizar_NotFound() throws Exception {
        when(comarcaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/comarcas/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comarca1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListarTodas() throws Exception {
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaService.listarTodas()).thenReturn(comarcas);

        mockMvc.perform(get("/api/comarcas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("São Paulo"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Campinas"));
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        when(comarcaService.buscarPorId(1L)).thenReturn(Optional.of(comarca1));

        mockMvc.perform(get("/api/comarcas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("São Paulo"));
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        when(comarcaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/comarcas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarPorNome() throws Exception {
        List<Comarca> comarcas = Arrays.asList(comarca1);
        when(comarcaService.buscarPorNome("São Paulo")).thenReturn(comarcas);

        mockMvc.perform(get("/api/comarcas/buscar/nome?nome=São Paulo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("São Paulo"));
    }

    @Test
    void testBuscarPorUfSigla() throws Exception {
        List<Comarca> comarcas = Arrays.asList(comarca1, comarca2);
        when(comarcaService.buscarPorUfSigla("SP")).thenReturn(comarcas);

        mockMvc.perform(get("/api/comarcas/buscar/uf/sigla/SP")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("São Paulo"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Campinas"));
    }

    @Test
    void testDeletar_Success() throws Exception {
        mockMvc.perform(delete("/api/comarcas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}