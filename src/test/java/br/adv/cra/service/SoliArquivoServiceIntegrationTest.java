package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.repository.SoliArquivoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class SoliArquivoServiceIntegrationTest {

    @Autowired
    private SoliArquivoService soliArquivoService;

    @MockBean
    private SoliArquivoRepository soliArquivoRepository;

    @MockBean
    private GoogleDriveService googleDriveService;

    @Test
    void testGetFileContentWithLocalFileNotFound() {
        // Prepare test data
        Long id = 1L;
        
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(id);
        soliArquivo.setStorageLocation("local");
        soliArquivo.setCaminhofisico("/non/existent/file.txt");
        
        // Configure mocks
        when(soliArquivoRepository.findById(id)).thenReturn(Optional.of(soliArquivo));
        
        // Execute the method and verify exception
        assertThrows(IOException.class, () -> {
            soliArquivoService.getFileContent(id);
        });
        
        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(id);
    }

    @Test
    void testGetFileContentWithGoogleDriveFileIdMissing() {
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
}