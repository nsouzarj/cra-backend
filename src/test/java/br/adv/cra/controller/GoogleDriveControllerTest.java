package br.adv.cra.controller;

import br.adv.cra.service.GoogleDriveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoogleDriveController.class)
public class GoogleDriveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleDriveService googleDriveService;

    @Test
    public void testGetAuthorizationUrl() throws Exception {
        mockMvc.perform(get("/api/google-drive/authorize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorizationUrl").exists());
    }

    @Test
    public void testGetConnectionStatus() throws Exception {
        mockMvc.perform(get("/api/google-drive/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").exists());
    }
}