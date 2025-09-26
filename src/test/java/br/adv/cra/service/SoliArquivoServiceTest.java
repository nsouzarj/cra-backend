package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SoliArquivoServiceTest {

    @Mock
    private SoliArquivoRepository soliArquivoRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;
    
    @Mock
    private GoogleDriveService googleDriveService;

    @InjectMocks
    private SoliArquivoService soliArquivoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Note: uploadDir is configured via Spring properties, not via a setter method
        // The actual upload directory will be determined by the application configuration
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
        String origem = "usuario";
        String storageLocation = "local";

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);

        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setNomearquivo("test.txt");
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setOrigem(origem);

        // Configure mocks
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivo);

        // Execute the method
        SoliArquivo result = soliArquivoService.salvarAnexo(file, solicitacaoId, origem, storageLocation);

        // Verify results
        assertNotNull(result);
        assertEquals("test.txt", result.getNomearquivo());
        assertEquals(origem, result.getOrigem());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(soliArquivoRepository, times(1)).save(any(SoliArquivo.class));
    }

    @Test
    void testSalvarAnexoBackwardCompatibility() throws IOException {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        Long solicitacaoId = 1L;
        String origem = "usuario";

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(solicitacaoId);

        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setNomearquivo("test.txt");
        soliArquivo.setDatainclusao(LocalDateTime.now());
        soliArquivo.setOrigem(origem);

        // Configure mocks
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivo);

        // Execute the method (backward compatibility version)
        SoliArquivo result = soliArquivoService.salvarAnexo(file, solicitacaoId, origem);

        // Verify results
        assertNotNull(result);
        assertEquals("test.txt", result.getNomearquivo());
        assertEquals(origem, result.getOrigem());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(soliArquivoRepository, times(1)).save(any(SoliArquivo.class));
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
        String origem = "usuario";
        String storageLocation = "local";

        // Configure mocks
        when(solicitacaoRepository.findById(solicitacaoId)).thenReturn(Optional.empty());

        // Execute the method and verify exception
        assertThrows(RuntimeException.class, () -> {
            soliArquivoService.salvarAnexo(file, solicitacaoId, origem, storageLocation);
        });

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(solicitacaoId);
        verify(soliArquivoRepository, never()).save(any(SoliArquivo.class));
    }

    @Test
    void testPodeDeletarCorrespondenteFile() {
        // Prepare test data
        Long id = 1L;
        String origem = "correspondente";

        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setOrigem("correspondente");

        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));

        // Execute the method
        boolean result = soliArquivoService.podeDeletar(id, origem);

        // Verify results
        assertTrue(result);
    }

    @Test
    void testPodeDeletarCorrespondenteCannotDeleteOtherFile() {
        // Prepare test data
        Long id = 1L;
        String origem = "correspondente";

        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setOrigem("usuario");

        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));

        // Execute the method
        boolean result = soliArquivoService.podeDeletar(id, origem);

        // Verify results
        assertFalse(result);
    }

    @Test
    void testPodeDeletarAdminCanDeleteAnyFile() {
        // Prepare test data
        Long id = 1L;
        String origem = "admin";

        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setOrigem("correspondente");

        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));

        // Execute the method
        boolean result = soliArquivoService.podeDeletar(id, origem);

        // Verify results
        assertTrue(result);
    }
    
    @Test
    void testGetFileContentLocalStorage() throws IOException {
        // Prepare test data
        Long id = 1L;
        
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setStorageLocation("local");
        soliArquivo.setCaminhofisico("/non/existent/file.txt");
        
        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));
        
        // Execute the method and verify exception (since we're not actually creating a file)
        assertThrows(IOException.class, () -> {
            soliArquivoService.getFileContent(id);
        });
        
        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(id);
    }
    
    @Test
    void testGetFileContentGoogleDrive() throws IOException {
        // Prepare test data
        Long id = 1L;
        String googleDriveFileId = "google-drive-file-id";
        
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setStorageLocation("google_drive");
        soliArquivo.setGoogleDriveFileId(googleDriveFileId);
        
        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));
        // Mock the GoogleDriveService to return an InputStream
        when(googleDriveService.downloadFile(googleDriveFileId)).thenReturn(new java.io.ByteArrayInputStream("test content".getBytes()));
        
        // Execute the method
        InputStream result = soliArquivoService.getFileContent(id);
        
        // Verify results
        assertNotNull(result);
        
        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(id);
        verify(googleDriveService, times(1)).downloadFile(googleDriveFileId);
    }
    
    @Test
    void testGetFileContentNotFound() {
        // Prepare test data
        Long id = 1L;
        
        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.empty());
        
        // Execute the method and verify exception
        assertThrows(RuntimeException.class, () -> {
            soliArquivoService.getFileContent(id);
        });
        
        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(id);
    }
    
    @Test
    void testGetFileContentGoogleDriveFileIdMissing() throws IOException {
        // Prepare test data
        Long id = 1L;
        
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setStorageLocation("google_drive");
        // Note: googleDriveFileId is null/missing
        
        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));
        
        // Execute the method and verify exception
        assertThrows(IOException.class, () -> {
            soliArquivoService.getFileContent(id);
        });
        
        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(id);
    }
}