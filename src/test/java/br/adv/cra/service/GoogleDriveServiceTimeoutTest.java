package br.adv.cra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveServiceTimeoutTest {

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
    void testCreateDriveServiceWithTimeoutHandling() {
        // This should throw an IOException rather than hanging
        assertThrows(java.io.IOException.class, () -> {
            googleDriveService.uploadFile(null);
        });
    }
}