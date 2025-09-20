package br.adv.cra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveServiceConnectivityTest {

    private GoogleDriveService googleDriveService;

    @BeforeEach
    void setUp() {
        googleDriveService = new GoogleDriveService();
        // Set the required fields using ReflectionTestUtils
        ReflectionTestUtils.setField(googleDriveService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(googleDriveService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(googleDriveService, "folderId", "test-folder-id");
    }

    @Test
    void testIsGoogleDriveAvailable() {
        // This test verifies that the isGoogleDriveAvailable method exists and can be called
        // Since we're using fake credentials, it should return false
        boolean isAvailable = googleDriveService.isGoogleDriveAvailable();
        // With fake credentials, we expect this to return false
        assertFalse(isAvailable, "With fake credentials, Google Drive should not be available");
    }
}