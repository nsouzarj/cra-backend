package br.adv.cra.service;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.repository.SoliArquivoRepository;
import br.adv.cra.repository.SolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoliArquivoServiceTest {

    @Mock
    private SoliArquivoRepository soliArquivoRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private GoogleDriveService googleDriveService;

    @InjectMocks
    private SoliArquivoService soliArquivoService;

    private SoliArquivo soliArquivoLocal;
    private SoliArquivo soliArquivoDrive;
    private Solicitacao solicitacao;
    private MultipartFile multipartFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        solicitacao = new Solicitacao();
        solicitacao.setId(1L);

        soliArquivoLocal = new SoliArquivo();
        soliArquivoLocal.setId(1L);
        soliArquivoLocal.setNomearquivo("local_test.pdf");
        soliArquivoLocal.setStorageLocation("local");
        soliArquivoLocal.setCaminhofisico("/fake/path/local_test.pdf");
        soliArquivoLocal.setOrigem("usuario");
        soliArquivoLocal.setSolicitacao(solicitacao);

        soliArquivoDrive = new SoliArquivo();
        soliArquivoDrive.setId(2L);
        soliArquivoDrive.setNomearquivo("drive_test.pdf");
        soliArquivoDrive.setStorageLocation("google_drive");
        soliArquivoDrive.setGoogleDriveFileId("fake-drive-id");
        soliArquivoDrive.setOrigem("correspondente");
        soliArquivoDrive.setSolicitacao(solicitacao);

        multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
    }

    @Test
    void salvar_ShouldSaveAndReturn() {
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivoLocal);
        SoliArquivo saved = soliArquivoService.salvar(soliArquivoLocal);
        assertNotNull(saved);
        assertEquals(soliArquivoLocal.getId(), saved.getId());
        verify(soliArquivoRepository).save(soliArquivoLocal);
    }

    @Test
    void atualizar_ShouldUpdate_WhenExists() {
        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivoLocal);
        SoliArquivo updated = soliArquivoService.atualizar(soliArquivoLocal);
        assertNotNull(updated);
        verify(soliArquivoRepository).save(soliArquivoLocal);
    }

    @Test
    void atualizar_ShouldThrowException_WhenNotExists() {
        when(soliArquivoRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> soliArquivoService.atualizar(soliArquivoLocal));
    }

    @Test
    void atualizarById_ShouldUpdate_WhenExists() {
        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenReturn(soliArquivoLocal);
        SoliArquivo updated = soliArquivoService.atualizar(1L, soliArquivoLocal);
        assertNotNull(updated);
        assertEquals(1L, updated.getId());
        verify(soliArquivoRepository).save(soliArquivoLocal);
    }

    @Test
    void deletar_ShouldDeleteFromGoogleDrive() throws IOException {
        when(soliArquivoRepository.existsById(2L)).thenReturn(true);
        when(soliArquivoRepository.findById(2L)).thenReturn(Optional.of(soliArquivoDrive));
        doNothing().when(googleDriveService).deleteFile("fake-drive-id");
        doNothing().when(soliArquivoRepository).deleteById(2L);

        soliArquivoService.deletar(2L);

        verify(googleDriveService).deleteFile("fake-drive-id");
        verify(soliArquivoRepository).deleteById(2L);
    }

    @Test
    void deletar_ShouldDeleteLocalFile() throws IOException {
        // Create a temporary file to simulate a real local file
        java.io.File tempFile = tempDir.resolve("local_test.pdf").toFile();
        tempFile.createNewFile();
        assertTrue(tempFile.exists());

        soliArquivoLocal.setCaminhofisico(tempFile.getAbsolutePath());

        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));
        doNothing().when(soliArquivoRepository).deleteById(1L);

        soliArquivoService.deletar(1L);

        verify(soliArquivoRepository).deleteById(1L);
        assertFalse(tempFile.exists(), "O arquivo local deveria ter sido deletado");
    }

    @Test
    void deletar_ShouldCatchException_WhenGoogleDriveFails() throws IOException {
        when(soliArquivoRepository.existsById(2L)).thenReturn(true);
        when(soliArquivoRepository.findById(2L)).thenReturn(Optional.of(soliArquivoDrive));
        doThrow(new IOException("Drive indisponível")).when(googleDriveService).deleteFile("fake-drive-id");

        // The service should catch the exception and proceed to delete from the repository
        soliArquivoService.deletar(2L);

        verify(soliArquivoRepository).deleteById(2L);
    }

    @Test
    void deletar_ShouldCatchException_WhenLocalFileDeleteFails() {
        // Use an invalid path to cause an exception during file deletion
        soliArquivoLocal.setCaminhofisico("invalid/path/that/will/fail");
        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));

        // The service should catch the exception and proceed to delete from the repository
        soliArquivoService.deletar(1L);

        verify(soliArquivoRepository).deleteById(1L);
    }

    @Test
    void deletar_ShouldThrowException_WhenNotExists() {
        when(soliArquivoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> soliArquivoService.deletar(99L));
    }

    @Test
    void buscarPorId_ShouldReturn_WhenFound() {
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));
        Optional<SoliArquivo> found = soliArquivoService.buscarPorId(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void listarTodos_ShouldReturnList() {
        when(soliArquivoRepository.findAll(any(Sort.class))).thenReturn(List.of(soliArquivoLocal));
        List<SoliArquivo> result = soliArquivoService.listarTodos();
        assertEquals(1, result.size());
    }

    @Test
    void listarTodos_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("nomearquivo");
        when(soliArquivoRepository.findAll(sort)).thenReturn(List.of(soliArquivoLocal));
        List<SoliArquivo> result = soliArquivoService.listarTodos(sort);
        assertEquals(1, result.size());
        verify(soliArquivoRepository).findAll(sort);
    }

    @Test
    void buscarPorSolicitacao_ShouldReturnList() {
        when(soliArquivoRepository.findBySolicitacao(any(Solicitacao.class), any(Sort.class))).thenReturn(List.of(soliArquivoLocal));
        List<SoliArquivo> result = soliArquivoService.buscarPorSolicitacao(solicitacao);
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorSolicitacao_WithSort_ShouldReturnSortedList() {
        Sort sort = Sort.by("nomearquivo");
        when(soliArquivoRepository.findBySolicitacao(solicitacao, sort)).thenReturn(List.of(soliArquivoLocal));
        List<SoliArquivo> result = soliArquivoService.buscarPorSolicitacao(solicitacao, sort);
        assertEquals(1, result.size());
        verify(soliArquivoRepository).findBySolicitacao(solicitacao, sort);
    }

    @Test
    void salvarAnexo_ShouldSaveToGoogleDrive() throws IOException {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(googleDriveService.uploadFile(multipartFile)).thenReturn("new-drive-id");
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenAnswer(i -> i.getArgument(0));

        SoliArquivo saved = soliArquivoService.salvarAnexo(multipartFile, 1L, "usuario", "google_drive");

        assertNotNull(saved);
        assertEquals("google_drive", saved.getStorageLocation());
        assertEquals("new-drive-id", saved.getGoogleDriveFileId());
        assertNull(saved.getCaminhofisico());
    }

    @Test
    void salvarAnexo_ShouldSaveLocally_WhenStorageIsLocal() throws IOException {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenAnswer(i -> i.getArgument(0));

        SoliArquivo saved = soliArquivoService.salvarAnexo(multipartFile, 1L, "usuario", "local");

        assertNotNull(saved);
        assertEquals("local", saved.getStorageLocation());
        assertNotNull(saved.getCaminhofisico());
        assertNull(saved.getGoogleDriveFileId());
    }

    @Test
    void salvarAnexo_ShouldCreateDirectory_WhenNotExists() throws IOException {
        java.io.File uploadDir = tempDir.resolve("uploads").toFile();
        assertFalse(uploadDir.exists()); // Ensure directory does not exist before test

        // Override user.dir to point to our temp directory
        System.setProperty("user.dir", tempDir.toString());

        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenAnswer(i -> i.getArgument(0));

        soliArquivoService.salvarAnexo(multipartFile, 1L, "usuario", "local");

        assertTrue(uploadDir.exists()); // Assert that the directory was created
    }

    @Test
    void salvarAnexo_ShouldThrowException_WhenSolicitacaoNotFound() {
        when(solicitacaoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> soliArquivoService.salvarAnexo(multipartFile, 99L, "usuario", "local"));
    }

    @Test
    void salvarAnexo_ShouldSaveLocally_WhenUsingBackwardCompatibility() throws IOException {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(soliArquivoRepository.save(any(SoliArquivo.class))).thenAnswer(i -> i.getArgument(0));

        SoliArquivo saved = soliArquivoService.salvarAnexo(multipartFile, 1L, "usuario");

        assertNotNull(saved);
        assertEquals("local", saved.getStorageLocation());
    }

    @Test
    void getFileContent_ShouldGetFromGoogleDrive() throws IOException {
        InputStream mockStream = new ByteArrayInputStream("drive data".getBytes());
        when(soliArquivoRepository.findById(2L)).thenReturn(Optional.of(soliArquivoDrive));
        when(googleDriveService.downloadFile("fake-drive-id")).thenReturn(mockStream);

        try (InputStream result = soliArquivoService.getFileContent(2L)) {
            assertNotNull(result);
            assertEquals("drive data", new String(result.readAllBytes()));
        }
    }

    @Test
    void getFileContent_ShouldGetFromLocalFile() throws IOException {
        java.io.File tempFile = tempDir.resolve("local_test.pdf").toFile();
        java.nio.file.Files.write(tempFile.toPath(), "local data".getBytes());
        soliArquivoLocal.setCaminhofisico(tempFile.getAbsolutePath());

        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));

        try (InputStream result = soliArquivoService.getFileContent(1L)) {
            assertNotNull(result);
            assertEquals("local data", new String(result.readAllBytes()));
        }
        
        System.setProperty("user.dir", System.getProperty("java.io.tmpdir")); // Reset user.dir
    }

    @Test
    void getFileContent_ShouldThrowException_WhenDriveIdIsNull() {
        soliArquivoDrive.setGoogleDriveFileId(null);
        when(soliArquivoRepository.findById(2L)).thenReturn(Optional.of(soliArquivoDrive));
        assertThrows(IOException.class, () -> soliArquivoService.getFileContent(2L));
    }

    @Test
    void getFileContent_ShouldThrowException_WhenFileEntityNotFound() {
        when(soliArquivoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> soliArquivoService.getFileContent(99L));
    }

    @Test
    void getFileContent_ShouldThrowException_WhenLocalPathIsNull() {
        soliArquivoLocal.setCaminhofisico(null);
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));
        assertThrows(IOException.class, () -> soliArquivoService.getFileContent(1L));
    }

    @Test
    void getFileContent_ShouldThrowException_WhenLocalFileNotFound() {
        // Set a path that doesn't exist
        soliArquivoLocal.setCaminhofisico(tempDir.resolve("non_existent_file.pdf").toString());
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));
        assertThrows(IOException.class, () -> soliArquivoService.getFileContent(1L));
    }

    @Test
    void listarAnexosPorSolicitacao_ShouldReturnList() {
        Sort sort = Sort.by("id");
        when(soliArquivoRepository.findBySolicitacaoIdsolicitacao(1L, sort)).thenReturn(List.of(soliArquivoLocal));
        List<SoliArquivo> result = soliArquivoService.listarAnexosPorSolicitacao(1L, sort);
        assertEquals(1, result.size());
        verify(soliArquivoRepository).findBySolicitacaoIdsolicitacao(1L, sort);
    }

    @Test
    void fileExists_ShouldReturnTrue_WhenExists() {
        when(soliArquivoRepository.existsById(1L)).thenReturn(true);
        assertTrue(soliArquivoService.fileExists(1L));
    }

    @Test
    void podeDeletar_ShouldReturnTrue_ForAdmin() {
        when(soliArquivoRepository.findById(anyLong())).thenReturn(Optional.of(soliArquivoLocal));
        assertTrue(soliArquivoService.podeDeletar(1L, "admin"));
    }

    @Test
    void podeDeletar_ShouldReturnTrue_ForSolicitante() {
        when(soliArquivoRepository.findById(anyLong())).thenReturn(Optional.of(soliArquivoLocal));
        assertTrue(soliArquivoService.podeDeletar(1L, "solicitante"));
    }

    @Test
    void podeDeletar_ShouldReturnTrue_WhenOwnerIsSame() {
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal));
        assertTrue(soliArquivoService.podeDeletar(1L, "usuario"));
    }

    @Test
    void podeDeletar_ShouldReturnFalse_WhenOwnerIsDifferent() {
        when(soliArquivoRepository.findById(1L)).thenReturn(Optional.of(soliArquivoLocal)); // origem "usuario"
        assertFalse(soliArquivoService.podeDeletar(1L, "correspondente"));
    }

    @Test
    void podeDeletar_ShouldReturnFalse_WhenFileNotExists() {
        when(soliArquivoRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(soliArquivoService.podeDeletar(99L, "admin"));
    }
}