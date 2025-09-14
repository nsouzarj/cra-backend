package br.adv.cra.controller;

import br.adv.cra.entity.SolicitacaoAnexo;
import br.adv.cra.entity.Usuario;
import br.adv.cra.service.SolicitacaoAnexoService;
import br.adv.cra.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SolicitacaoAnexoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SolicitacaoAnexoService solicitacaoAnexoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private SolicitacaoAnexoController solicitacaoAnexoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(solicitacaoAnexoController).build();

        // Set up security context
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
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

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("testuser");

        SolicitacaoAnexo anexo = new SolicitacaoAnexo();
        anexo.setIdarquivoanexo(1L);
        anexo.setNomearquivo("test.txt");
        anexo.setTipoarquivo("text/plain");
        anexo.setDatasolicitacao(LocalDateTime.now());

        // Configure mocks
        when(usuarioService.buscarPorLogin("testuser")).thenReturn(Optional.of(usuario));
        when(solicitacaoAnexoService.salvarAnexo(any(), eq(solicitacaoId), any(Usuario.class))).thenReturn(anexo);

        // Execute the request
        mockMvc.perform(multipart("/api/solicitacoes-anexos/upload")
                .file(file)
                .param("solicitacaoId", solicitacaoId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idarquivoanexo").value(1L))
                .andExpect(jsonPath("$.nomearquivo").value("test.txt"));

        // Verify interactions
        verify(usuarioService, times(1)).buscarPorLogin("testuser");
        verify(solicitacaoAnexoService, times(1)).salvarAnexo(any(), eq(solicitacaoId), any(Usuario.class));
    }

    @Test
    void testUploadAnexoUsuarioNotFound() throws Exception {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        Long solicitacaoId = 1L;

        // Configure mocks
        when(usuarioService.buscarPorLogin("testuser")).thenReturn(Optional.empty());

        // Execute the request
        mockMvc.perform(multipart("/api/solicitacoes-anexos/upload")
                .file(file)
                .param("solicitacaoId", solicitacaoId.toString()))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(usuarioService, times(1)).buscarPorLogin("testuser");
        verify(solicitacaoAnexoService, never()).salvarAnexo(any(), anyLong(), any(Usuario.class));
    }
}