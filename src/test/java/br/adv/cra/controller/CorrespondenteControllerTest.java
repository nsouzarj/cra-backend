package br.adv.cra.controller;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.service.CorrespondenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CorrespondenteControllerTest {

    @Mock
    private CorrespondenteService correspondenteService;

    @InjectMocks
    private CorrespondenteController correspondenteController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListarTodosSuccess() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Test Correspondente 1");
        
        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Test Correspondente 2");
        
        List<Correspondente> correspondentes = Arrays.asList(correspondente1, correspondente2);
        
        when(correspondenteService.listarTodos()).thenReturn(correspondentes);
        
        // When
        ResponseEntity<List<Correspondente>> response = correspondenteController.listarTodos();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }
    
    @Test
    public void testListarTodosException() {
        // Given
        when(correspondenteService.listarTodos()).thenThrow(new RuntimeException("Database error"));
        
        // When
        ResponseEntity<List<Correspondente>> response = correspondenteController.listarTodos();
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}