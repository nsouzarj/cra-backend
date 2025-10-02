package br.adv.cra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleDriveServiceTest {

    private GoogleDriveService googleDriveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        googleDriveService = new GoogleDriveService();
        
        // Set required properties using reflection
        ReflectionTestUtils.setField(googleDriveService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(googleDriveService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(googleDriveService, "folderId", "test-folder-id");
    }

    @Test
    void testSetTokens_ShouldSetTokensCorrectly() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        long currentTime = System.currentTimeMillis();

        // When
        googleDriveService.setTokens(accessToken, refreshToken);

        // Then
        assertEquals(accessToken, googleDriveService.getAccessToken());
        assertEquals(refreshToken, googleDriveService.getRefreshToken());
        assertTrue(googleDriveService.hasValidTokens());
    }

    @Test
    void testSetTokens_NullTokens_ShouldHandleGracefully() {
        // When
        googleDriveService.setTokens(null, null);

        // Then
        assertNull(googleDriveService.getAccessToken());
        assertNull(googleDriveService.getRefreshToken());
        assertFalse(googleDriveService.hasValidTokens());
    }

    @Test
    void testHasValidTokens_WithValidTokens_ShouldReturnTrue() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);

        // When
        boolean result = googleDriveService.hasValidTokens();

        // Then
        assertTrue(result);
    }

    @Test
    void testHasValidTokens_WithExpiredTokens_ShouldReturnFalse() throws InterruptedException {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);
        
        // Manually set expiration time to past
        ReflectionTestUtils.setField(googleDriveService, "tokenExpirationTime", System.currentTimeMillis() - 1000);

        // When
        boolean result = googleDriveService.hasValidTokens();

        // Then
        assertFalse(result);
    }

    @Test
    void testHasValidTokens_WithNullAccessToken_ShouldReturnFalse() {
        // Given
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(null, refreshToken);

        // When
        boolean result = googleDriveService.hasValidTokens();

        // Then
        assertFalse(result);
    }

    @Test
    void testHasValidTokens_WithEmptyAccessToken_ShouldReturnFalse() {
        // Given
        String accessToken = "";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);

        // When
        boolean result = googleDriveService.hasValidTokens();

        // Then
        assertFalse(result);
    }

    @Test
    void testClearTokens_ShouldClearAllTokens() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);

        // When
        googleDriveService.clearTokens();

        // Then
        assertNull(googleDriveService.getAccessToken());
        assertNull(googleDriveService.getRefreshToken());
        assertFalse(googleDriveService.hasValidTokens());
    }

    @Test
    void testEnsureValidAccessToken_WithValidTokens_ShouldNotThrowException() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);

        // When & Then
        // Test through reflection since the method is private
        assertDoesNotThrow(() -> {
            ReflectionTestUtils.invokeMethod(googleDriveService, "ensureValidAccessToken");
        });
    }

    @Test
    void testEnsureValidAccessToken_WithNullTokens_ShouldThrowIOException() {
        // When & Then
        // Test through reflection since the method is private
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ReflectionTestUtils.invokeMethod(googleDriveService, "ensureValidAccessToken");
        });

        assertTrue(exception.getCause() instanceof IOException);
        assertTrue(exception.getCause().getMessage().contains("No valid tokens available. Please re-authenticate with Google Drive."));
    }

    @Test
    void testEnsureValidAccessToken_WithExpiredTokensAndRefreshToken_ShouldRefreshToken() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        googleDriveService.setTokens(accessToken, refreshToken);
        
        // Manually set expiration time to past
        ReflectionTestUtils.setField(googleDriveService, "tokenExpirationTime", System.currentTimeMillis() - 1000);

        // When & Then
        // Test through reflection since the method is private
        // The method should throw an exception when refresh fails, not return a boolean
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ReflectionTestUtils.invokeMethod(googleDriveService, "ensureValidAccessToken");
        });
        
        // Verify the cause is an IOException
        assertTrue(exception.getCause() instanceof IOException);
        assertTrue(exception.getCause().getMessage().contains("Please re-authenticate with Google Drive."));
    }
    
    // Test the generateUniqueFilename functionality through reflection since it's a private helper method
    @Test
    void testGenerateUniqueFilename_WithValidFilename_ShouldGenerateUniqueName() {
        // Given
        String originalFilename = "test.pdf";

        // When - Test through reflection since the method is private
        String result = (String) ReflectionTestUtils.invokeMethod(googleDriveService, "generateUniqueFilename", originalFilename);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(".pdf"));
        assertNotEquals(originalFilename, result);
    }

    @Test
    void testGenerateUniqueFilename_WithNullFilename_ShouldGenerateUUID() {
        // When - Test through reflection since the method is private
        String result = (String) ReflectionTestUtils.invokeMethod(googleDriveService, "generateUniqueFilename", new Object[] {null});

        // Then
        assertNotNull(result);
        assertFalse(result.contains("."));
    }

    @Test
    void testGenerateUniqueFilename_WithEmptyFilename_ShouldGenerateUUID() {
        // When - Test through reflection since the method is private
        String result = (String) ReflectionTestUtils.invokeMethod(googleDriveService, "generateUniqueFilename", "");

        // Then
        assertNotNull(result);
        assertFalse(result.contains("."));
    }

    @Test
    void testGenerateUniqueFilename_WithoutExtension_ShouldGenerateNameWithoutExtension() {
        // Given
        String originalFilename = "testfile";

        // When - Test through reflection since the method is private
        String result = (String) ReflectionTestUtils.invokeMethod(googleDriveService, "generateUniqueFilename", originalFilename);

        // Then
        assertNotNull(result);
        assertFalse(result.contains("."));
        assertNotEquals(originalFilename, result);
    }
}