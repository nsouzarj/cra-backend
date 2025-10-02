package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SoliArquivoServiceTest {

    @Mock
    private SoliArquivoRepository soliArquivoRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private GoogleDriveService googleDriveService;

    @Mock
    private MultipartFile mockMultipartFile;

    private SoliArquivoService soliArquivoService;

    private SoliArquivo testSoliArquivo;
    private Solicitacao testSolicitacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        soliArquivoService = new SoliArquivoService(soliArquivoRepository, solicitacaoRepository, googleDriveService);

        // Create test entities
        testSolicitacao = new Solicitacao();
        testSolicitacao.setId(1L);

        testSoliArquivo = new SoliArquivo();
        testSoliArquivo.setId(1L);
        testSoliArquivo.setSolicitacao(testSolicitacao);
        testSoliArquivo.setNomearquivo("test-file.pdf");
        testSoliArquivo.setDatainclusao(LocalDateTime.now());
        testSoliArquivo.setOrigem("usuario");
        testSoliArquivo.setAtivo(true);
    }

    @Test
    void testSalvar_ShouldSaveSoliArquivo() {
        // Given
        SoliArquivo soliArquivoToSave = new SoliArquivo();
        soliArquivoToSave.setNomearquivo("new-file.pdf");

        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(testSoliArquivo);

        // When
        SoliArquivo result = soliArquivoService.salvar(soliArquivoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testSoliArquivo, result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).save(soliArquivoToSave);
    }

    @Test
    void testAtualizar_ExistingSoliArquivo_ShouldUpdateSoliArquivo() {
        // Given
        SoliArquivo soliArquivoToUpdate = new SoliArquivo();
        soliArquivoToUpdate.setId(1L);
        soliArquivoToUpdate.setNomearquivo("updated-file.pdf");

        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivoToUpdate);

        // When
        SoliArquivo result = soliArquivoService.atualizar(soliArquivoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals(soliArquivoToUpdate, result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(1L);
        verify(soliArquivoRepository, times(1)).save(soliArquivoToUpdate);
    }

    @Test
    void testAtualizar_NonExistentSoliArquivo_ShouldThrowException() {
        // Given
        SoliArquivo soliArquivoToUpdate = new SoliArquivo();
        soliArquivoToUpdate.setId(999L);

        when(soliArquivoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            soliArquivoService.atualizar(soliArquivoToUpdate);
        });

        assertEquals("Arquivo não encontrado", exception.getMessage());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(999L);
        verify(soliArquivoRepository, never()).save(any(SoliArquivo.class));
    }

    @Test
    void testAtualizarWithId_ExistingSoliArquivo_ShouldUpdateSoliArquivo() {
        // Given
        SoliArquivo soliArquivoToUpdate = new SoliArquivo();
        soliArquivoToUpdate.setNomearquivo("updated-file.pdf");

        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(testSoliArquivo);

        // When
        SoliArquivo result = soliArquivoService.atualizar(1L, soliArquivoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals(testSoliArquivo, result);
        assertEquals(1L, result.getId());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(1L);
        verify(soliArquivoRepository, times(1)).save(any(SoliArquivo.class));
    }

    @Test
    void testAtualizarWithId_NonExistentSoliArquivo_ShouldThrowException() {
        // Given
        SoliArquivo soliArquivoToUpdate = new SoliArquivo();
        soliArquivoToUpdate.setNomearquivo("updated-file.pdf");

        when(soliArquivoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            soliArquivoService.atualizar(999L, soliArquivoToUpdate);
        });

        assertEquals("Arquivo não encontrado", exception.getMessage());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(999L);
        verify(soliArquivoRepository, never()).save(any(SoliArquivo.class));
    }

    @Test
    void testDeletar_ExistingSoliArquivoWithLocalStorage_ShouldDeleteSoliArquivo() throws IOException {
        // Given
        SoliArquivo soliArquivoToDelete = new SoliArquivo();
        soliArquivoToDelete.setId(1L);
        soliArquivoToDelete.setCaminhofisico("/path/to/file.pdf");
        soliArquivoToDelete.setStorageLocation("local");

        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoToDelete));

        // When
        soliArquivoService.deletar(1L);

        // Then
        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(1L);
        verify(soliArquivoRepository, times(1)).findById(1L);
        verify(soliArquivoRepository, times(1)).deleteById(1L);
        verifyNoInteractions(googleDriveService);
    }

    @Test
    void testDeletar_ExistingSoliArquivoWithGoogleDriveStorage_ShouldDeleteSoliArquivo() throws IOException {
        // Given
        SoliArquivo soliArquivoToDelete = new SoliArquivo();
        soliArquivoToDelete.setId(1L);
        soliArquivoToDelete.setGoogleDriveFileId("google-drive-file-id");
        soliArquivoToDelete.setStorageLocation("google_drive");

        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoToDelete));
        doNothing().when(googleDriveService).deleteFile("google-drive-file-id");

        // When
        soliArquivoService.deletar(1L);

        // Then
        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(1L);
        verify(soliArquivoRepository, times(1)).findById(1L);
        verify(googleDriveService, times(1)).deleteFile("google-drive-file-id");
        verify(soliArquivoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentSoliArquivo_ShouldThrowException() {
        // Given
        when(soliArquivoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            soliArquivoService.deletar(999L);
        });

        assertEquals("Arquivo não encontrado", exception.getMessage());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(999L);
        verify(soliArquivoRepository, never()).findById(anyLong());
        verify(soliArquivoRepository, never()).deleteById(anyLong());
        verifyNoInteractions(googleDriveService);
    }

    @Test
    void testBuscarPorId_ExistingSoliArquivo_ShouldReturnSoliArquivo() {
        // Given
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(testSoliArquivo));

        // When
        Optional<SoliArquivo> result = soliArquivoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSoliArquivo, result.get());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentSoliArquivo_ShouldReturnEmpty() {
        // Given
        when(soliArquivoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<SoliArquivo> result = soliArquivoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodos_ShouldReturnAllSoliArquivos() {
        // Given
        SoliArquivo soliArquivo1 = new SoliArquivo();
        soliArquivo1.setId(1L);
        soliArquivo1.setNomearquivo("file1.pdf");

        SoliArquivo soliArquivo2 = new SoliArquivo();
        soliArquivo2.setId(2L);
        soliArquivo2.setNomearquivo("file2.pdf");

        when(soliArquivoRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(soliArquivo1, soliArquivo2));

        // When
        List<SoliArquivo> result = soliArquivoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.pdf", result.get(0).getNomearquivo());
        assertEquals("file2.pdf", result.get(1).getNomearquivo());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testListarTodosWithSort_ShouldReturnAllSoliArquivosWithCustomSorting() {
        // Given
        SoliArquivo soliArquivo1 = new SoliArquivo();
        soliArquivo1.setId(1L);
        soliArquivo1.setNomearquivo("file1.pdf");

        SoliArquivo soliArquivo2 = new SoliArquivo();
        soliArquivo2.setId(2L);
        soliArquivo2.setNomearquivo("file2.pdf");

        Sort sort = Sort.by(Sort.Direction.ASC, "nomearquivo");

        when(soliArquivoRepository.findAll(sort)).thenReturn(Arrays.asList(soliArquivo1, soliArquivo2));

        // When
        List<SoliArquivo> result = soliArquivoService.listarTodos(sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.pdf", result.get(0).getNomearquivo());
        assertEquals("file2.pdf", result.get(1).getNomearquivo());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findAll(sort);
    }

    @Test
    void testBuscarPorSolicitacao_ShouldReturnSoliArquivosForSolicitacao() {
        // Given
        SoliArquivo soliArquivo1 = new SoliArquivo();
        soliArquivo1.setId(1L);
        soliArquivo1.setSolicitacao(testSolicitacao);
        soliArquivo1.setNomearquivo("file1.pdf");

        SoliArquivo soliArquivo2 = new SoliArquivo();
        soliArquivo2.setId(2L);
        soliArquivo2.setSolicitacao(testSolicitacao);
        soliArquivo2.setNomearquivo("file2.pdf");

        when(soliArquivoRepository.findBySolicitacao(eq(testSolicitacao), any(Sort.class)))
            .thenReturn(Arrays.asList(soliArquivo1, soliArquivo2));

        // When
        List<SoliArquivo> result = soliArquivoService.buscarPorSolicitacao(testSolicitacao);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testSolicitacao, result.get(0).getSolicitacao());
        assertEquals(testSolicitacao, result.get(1).getSolicitacao());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findBySolicitacao(eq(testSolicitacao), any(Sort.class));
    }

    @Test
    void testBuscarPorSolicitacaoWithSort_ShouldReturnSoliArquivosForSolicitacaoWithCustomSorting() {
        // Given
        SoliArquivo soliArquivo1 = new SoliArquivo();
        soliArquivo1.setId(1L);
        soliArquivo1.setSolicitacao(testSolicitacao);
        soliArquivo1.setNomearquivo("file1.pdf");

        SoliArquivo soliArquivo2 = new SoliArquivo();
        soliArquivo2.setId(2L);
        soliArquivo2.setSolicitacao(testSolicitacao);
        soliArquivo2.setNomearquivo("file2.pdf");

        Sort sort = Sort.by(Sort.Direction.ASC, "nomearquivo");

        when(soliArquivoRepository.findBySolicitacao(eq(testSolicitacao), eq(sort)))
            .thenReturn(Arrays.asList(soliArquivo1, soliArquivo2));

        // When
        List<SoliArquivo> result = soliArquivoService.buscarPorSolicitacao(testSolicitacao, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testSolicitacao, result.get(0).getSolicitacao());
        assertEquals(testSolicitacao, result.get(1).getSolicitacao());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findBySolicitacao(eq(testSolicitacao), eq(sort));
    }

    @Test
    void testSalvarAnexo_WithValidFileAndSolicitacao_ShouldSaveSoliArquivo() throws IOException {
        // Given
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-file.pdf");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(testSoliArquivo);

        // When
        SoliArquivo result = soliArquivoService.salvarAnexo(mockMultipartFile, 1L, "usuario");

        // Then
        assertNotNull(result);
        assertEquals(testSoliArquivo, result);

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(soliArquivoRepository, times(1)).save(any(SoliArquivo.class));
    }

    @Test
    void testSalvarAnexo_WithInvalidSolicitacao_ShouldThrowException() throws IOException {
        // Given
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            soliArquivoService.salvarAnexo(mockMultipartFile, 999L, "usuario");
        });

        assertEquals("Solicitação não encontrada", exception.getMessage());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(999L);
        verify(soliArquivoRepository, never()).save(any(SoliArquivo.class));
    }

    @Test
    void testSalvarAnexoWithStorageLocation_WithGoogleDriveStorage_ShouldSaveSoliArquivo() throws IOException {
        // Given
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-file.pdf");
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(testSolicitacao));
        when(googleDriveService.uploadFile(mockMultipartFile)).thenReturn("google-drive-file-id");
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenAnswer(invocation -> {
            SoliArquivo soliArquivo = invocation.getArgument(0);
            soliArquivo.setId(1L); // Set ID as repository would
            soliArquivo.setGoogleDriveFileId("google-drive-file-id"); // Set the Google Drive file ID
            return soliArquivo;
        });

        // When
        SoliArquivo result = soliArquivoService.salvarAnexo(mockMultipartFile, 1L, "usuario", "google_drive");

        // Then
        assertNotNull(result);
        assertEquals("google-drive-file-id", result.getGoogleDriveFileId());
        assertEquals(testSolicitacao, result.getSolicitacao());
        assertEquals("test-file.pdf", result.getNomearquivo());
        assertEquals("google_drive", result.getStorageLocation());
        assertNotNull(result.getDatainclusao());
        assertEquals("usuario", result.getOrigem());
        assertTrue(result.isAtivo());

        // Verify interactions
        verify(solicitacaoRepository, times(1)).findById(1L);
        verify(googleDriveService, times(1)).uploadFile(mockMultipartFile);
        verify(soliArquivoRepository, times(1)).save(any(SoliArquivo.class));
    }

    @Test
    void testListarAnexosPorSolicitacao_ShouldReturnSoliArquivosForSolicitacaoId() {
        // Given
        SoliArquivo soliArquivo1 = new SoliArquivo();
        soliArquivo1.setId(1L);
        soliArquivo1.setSolicitacao(testSolicitacao);
        soliArquivo1.setNomearquivo("file1.pdf");

        SoliArquivo soliArquivo2 = new SoliArquivo();
        soliArquivo2.setId(2L);
        soliArquivo2.setSolicitacao(testSolicitacao);
        soliArquivo2.setNomearquivo("file2.pdf");

        Sort sort = Sort.by(Sort.Direction.DESC, "datainclusao");

        when(soliArquivoRepository.findBySolicitacaoIdsolicitacao(eq(1L), any(Sort.class)))
            .thenReturn(Arrays.asList(soliArquivo1, soliArquivo2));

        // When
        List<SoliArquivo> result = soliArquivoService.listarAnexosPorSolicitacao(1L, sort);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getSolicitacao().getId());
        assertEquals(1L, result.get(1).getSolicitacao().getId());

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findBySolicitacaoIdsolicitacao(eq(1L), any(Sort.class));
    }

    @Test
    void testFileExists_ExistingFile_ShouldReturnTrue() {
        // Given
        when(soliArquivoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = soliArquivoService.fileExists(1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(1L);
    }

    @Test
    void testFileExists_NonExistentFile_ShouldReturnFalse() {
        // Given
        when(soliArquivoRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = soliArquivoService.fileExists(999L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).existsById(999L);
    }

    @Test
    void testPodeDeletar_AdminUser_ShouldReturnTrue() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("usuario");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "admin");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_SolicitanteUser_ShouldReturnTrue() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("usuario");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "solicitante");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_UsuarioUserWithUsuarioOrigin_ShouldReturnTrue() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("usuario");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "usuario");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_UsuarioUserWithDifferentOrigin_ShouldReturnFalse() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("correspondente");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "usuario");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_CorrespondenteUserWithCorrespondenteOrigin_ShouldReturnTrue() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("correspondente");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "correspondente");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_CorrespondenteUserWithDifferentOrigin_ShouldReturnFalse() {
        // Given
        SoliArquivo soliArquivo = new SoliArquivo();
        soliArquivo.setId(1L);
        soliArquivo.setOrigem("usuario");

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivo));

        // When
        boolean result = soliArquivoService.podeDeletar(1L, "correspondente");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(1L);
    }

    @Test
    void testPodeDeletar_NonExistentFile_ShouldReturnFalse() {
        // Given
        when(soliArquivoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = soliArquivoService.podeDeletar(999L, "usuario");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(soliArquivoRepository, times(1)).findById(999L);
    }
}