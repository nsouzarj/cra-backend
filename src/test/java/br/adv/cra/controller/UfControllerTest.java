package br.adv.cra.controller;

import br.adv.cra.entity.Uf;
import br.adv.cra.service.UfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

// Completely standalone test without Spring context
class UfControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UfService ufService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Uf uf1;
    private Uf uf2;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Manual setup without Spring context
        UfController ufController = new UfController(ufService);
        mockMvc = standaloneSetup(ufController).build();
        
        uf1 = new Uf(1L, "SP", "São Paulo");
        uf2 = new Uf(2L, "RJ", "Rio de Janeiro");
    }

    @Test
    void testListarTodas() throws Exception {
        List<Uf> ufs = Arrays.asList(uf1, uf2);
        when(ufService.listarTodas()).thenReturn(ufs);

        mockMvc.perform(get("/api/ufs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sigla").value("SP"))
                .andExpect(jsonPath("$[0].nome").value("São Paulo"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].sigla").value("RJ"))
                .andExpect(jsonPath("$[1].nome").value("Rio de Janeiro"));
    }

    @Test
    void testBuscarPorId_Success() throws Exception {
        when(ufService.buscarPorId(1L)).thenReturn(Optional.of(uf1));

        mockMvc.perform(get("/api/ufs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sigla").value("SP"))
                .andExpect(jsonPath("$.nome").value("São Paulo"));
    }

    @Test
    void testBuscarPorId_NotFound() throws Exception {
        when(ufService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ufs/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarPorSigla_Success() throws Exception {
        when(ufService.buscarPorSigla("SP")).thenReturn(Optional.of(uf1));

        mockMvc.perform(get("/api/ufs/sigla/SP")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sigla").value("SP"))
                .andExpect(jsonPath("$.nome").value("São Paulo"));
    }

    @Test
    void testBuscarPorSigla_NotFound() throws Exception {
        when(ufService.buscarPorSigla("ZZ")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ufs/sigla/ZZ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarPorNome() throws Exception {
        List<Uf> ufs = Arrays.asList(uf1);
        when(ufService.buscarPorNome("São Paulo")).thenReturn(ufs);

        mockMvc.perform(get("/api/ufs/buscar/nome?nome=São Paulo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sigla").value("SP"))
                .andExpect(jsonPath("$[0].nome").value("São Paulo"));
    }
}