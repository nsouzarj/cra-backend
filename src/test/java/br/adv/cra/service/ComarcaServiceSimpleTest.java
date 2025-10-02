package br.adv.cra.service;

import br.adv.cra.entity.Comarca;
import br.adv.cra.repository.ComarcaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComarcaServiceSimpleTest {

    @Mock
    private ComarcaRepository comarcaRepository;

    @InjectMocks
    private ComarcaService comarcaService;

    private Comarca comarca;

    @BeforeEach
    void setUp() {
        comarca = new Comarca();
        comarca.setId(1L);
        comarca.setNome("Test Comarca");
    }

    @Test
    void salvar_ShouldSaveAndReturnComarca() {
        // Given
        when(comarcaRepository.save(any(Comarca.class))).thenReturn(comarca);

        // When
        Comarca result = comarcaService.salvar(comarca);

        // Then
        assertNotNull(result);
        assertEquals(comarca.getId(), result.getId());
        assertEquals(comarca.getNome(), result.getNome());
        verify(comarcaRepository, times(1)).save(comarca);
    }
}