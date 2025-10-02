package br.adv.cra.service;

import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    private EnderecoService enderecoService;

    private Endereco testEndereco;
    private Uf testUf;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enderecoService = new EnderecoService(enderecoRepository);

        // Create test UF
        testUf = new Uf();
        testUf.setId(1L);
        testUf.setSigla("SP");
        testUf.setNome("São Paulo");

        // Create test endereco
        testEndereco = new Endereco();
        testEndereco.setId(1L);
        testEndereco.setLogradouro("Rua Teste");
        testEndereco.setNumero("123");
        testEndereco.setBairro("Centro");
        testEndereco.setCidade("São Paulo");
        testEndereco.setUf(testUf);
        testEndereco.setCep("12345-678");
    }

    @Test
    void testSalvar_ShouldSaveEndereco() {
        // Given
        Endereco enderecoToSave = new Endereco();
        enderecoToSave.setLogradouro("Rua Nova");
        enderecoToSave.setNumero("456");
        enderecoToSave.setBairro("Novo Bairro");
        enderecoToSave.setCidade("São Paulo");
        enderecoToSave.setUf(testUf);
        enderecoToSave.setCep("87654-321");

        when(enderecoRepository.save(any(Endereco.class))).thenReturn(testEndereco);

        // When
        Endereco result = enderecoService.salvar(enderecoToSave);

        // Then
        assertNotNull(result);
        assertEquals(testEndereco, result);

        // Verify interactions
        verify(enderecoRepository, times(1)).save(enderecoToSave);
    }

    @Test
    void testAtualizar_ExistingEndereco_ShouldUpdateEndereco() {
        // Given
        Endereco enderecoToUpdate = new Endereco();
        enderecoToUpdate.setId(1L);
        enderecoToUpdate.setLogradouro("Rua Atualizada");
        enderecoToUpdate.setNumero("789");
        enderecoToUpdate.setBairro("Bairro Atualizado");
        enderecoToUpdate.setCidade("São Paulo");
        enderecoToUpdate.setUf(testUf);
        enderecoToUpdate.setCep("11111-111");

        when(enderecoRepository.existsById(1L)).thenReturn(true);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoToUpdate);

        // When
        Endereco result = enderecoService.atualizar(enderecoToUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Rua Atualizada", result.getLogradouro());
        assertEquals("Bairro Atualizado", result.getBairro());

        // Verify interactions
        verify(enderecoRepository, times(1)).existsById(1L);
        verify(enderecoRepository, times(1)).save(enderecoToUpdate);
    }

    @Test
    void testAtualizar_NonExistentEndereco_ShouldThrowException() {
        // Given
        Endereco enderecoToUpdate = new Endereco();
        enderecoToUpdate.setId(999L);
        enderecoToUpdate.setLogradouro("Non-existent");

        when(enderecoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            enderecoService.atualizar(enderecoToUpdate);
        });

        assertEquals("Endereço não encontrado", exception.getMessage());

        // Verify interactions
        verify(enderecoRepository, times(1)).existsById(999L);
        verify(enderecoRepository, never()).save(any(Endereco.class));
    }

    @Test
    void testDeletar_ExistingEndereco_ShouldDeleteEndereco() {
        // Given
        when(enderecoRepository.existsById(1L)).thenReturn(true);

        // When
        enderecoService.deletar(1L);

        // Then
        // Verify interactions
        verify(enderecoRepository, times(1)).existsById(1L);
        verify(enderecoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentEndereco_ShouldThrowException() {
        // Given
        when(enderecoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            enderecoService.deletar(999L);
        });

        assertEquals("Endereço não encontrado", exception.getMessage());

        // Verify interactions
        verify(enderecoRepository, times(1)).existsById(999L);
        verify(enderecoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorId_ExistingEndereco_ShouldReturnEndereco() {
        // Given
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(testEndereco));

        // When
        Optional<Endereco> result = enderecoService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEndereco, result.get());

        // Verify interactions
        verify(enderecoRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentEndereco_ShouldReturnEmpty() {
        // Given
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Endereco> result = enderecoService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());

        // Verify interactions
        verify(enderecoRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodos_ShouldReturnAllEnderecos() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Rua 1");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setLogradouro("Rua 2");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        when(enderecoRepository.findAll()).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        List<Endereco> result = enderecoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rua 1", result.get(0).getLogradouro());
        assertEquals("Rua 2", result.get(1).getLogradouro());

        // Verify interactions
        verify(enderecoRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorCidade_ShouldReturnMatchingEnderecos() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Rua 1");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setLogradouro("Rua 2");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        // Note: The repository method name suggests it should be "findByCidadeContaining"
        // but the actual implementation might be different due to query issues
        when(enderecoRepository.findByCidadeContaining("São Paulo")).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        List<Endereco> result = enderecoService.buscarPorCidade("São Paulo");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("São Paulo", result.get(0).getCidade());
        assertEquals("São Paulo", result.get(1).getCidade());

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCidadeContaining("São Paulo");
    }

    @Test
    void testBuscarPorBairro_ShouldReturnMatchingEnderecos() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Rua 1");
        endereco1.setBairro("Centro");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setLogradouro("Rua 2");
        endereco2.setBairro("Centro");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        when(enderecoRepository.findByBairroContaining("Centro")).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        List<Endereco> result = enderecoService.buscarPorBairro("Centro");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Centro", result.get(0).getBairro());
        assertEquals("Centro", result.get(1).getBairro());

        // Verify interactions
        verify(enderecoRepository, times(1)).findByBairroContaining("Centro");
    }

    @Test
    void testBuscarPorCep_ShouldReturnMatchingEnderecos() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Rua 1");
        endereco1.setCep("12345-678");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setLogradouro("Rua 2");
        endereco2.setCep("12345-678");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        when(enderecoRepository.findByCep("12345-678")).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        List<Endereco> result = enderecoService.buscarPorCep("12345-678");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12345-678", result.get(0).getCep());
        assertEquals("12345-678", result.get(1).getCep());

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("12345-678");
    }

    @Test
    void testBuscarPorLogradouro_ShouldReturnMatchingEnderecos() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Avenida Paulista");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setLogradouro("Avenida Paulista");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        when(enderecoRepository.findByLogradouroContaining("Avenida Paulista")).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        List<Endereco> result = enderecoService.buscarPorLogradouro("Avenida Paulista");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getLogradouro().contains("Avenida Paulista"));
        assertTrue(result.get(1).getLogradouro().contains("Avenida Paulista"));

        // Verify interactions
        verify(enderecoRepository, times(1)).findByLogradouroContaining("Avenida Paulista");
    }

    @Test
    void testExisteCep_ExistingCep_ShouldReturnTrue() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setLogradouro("Rua Teste");
        endereco.setCep("12345-678");
        endereco.setCidade("São Paulo");
        endereco.setUf(testUf);

        when(enderecoRepository.findByCep("12345-678")).thenReturn(Arrays.asList(endereco));

        // When
        boolean result = enderecoService.existeCep("12345-678");

        // Then
        assertTrue(result);

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("12345-678");
    }

    @Test
    void testExisteCep_NonExistentCep_ShouldReturnFalse() {
        // Given
        when(enderecoRepository.findByCep("99999-999")).thenReturn(Arrays.asList());

        // When
        boolean result = enderecoService.existeCep("99999-999");

        // Then
        assertFalse(result);

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("99999-999");
    }

    @Test
    void testExisteCepParaOutroEndereco_ExistingCepForDifferentEndereco_ShouldReturnTrue() {
        // Given
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setLogradouro("Rua 1");
        endereco1.setCep("12345-678");
        endereco1.setCidade("São Paulo");
        endereco1.setUf(testUf);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L); // Different ID
        endereco2.setLogradouro("Rua 2");
        endereco2.setCep("12345-678");
        endereco2.setCidade("São Paulo");
        endereco2.setUf(testUf);

        when(enderecoRepository.findByCep("12345-678")).thenReturn(Arrays.asList(endereco1, endereco2));

        // When
        boolean result = enderecoService.existeCepParaOutroEndereco("12345-678", 1L);

        // Then
        assertTrue(result);

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("12345-678");
    }

    @Test
    void testExisteCepParaOutroEndereco_ExistingCepForSameEndereco_ShouldReturnFalse() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setId(1L); // Same ID
        endereco.setLogradouro("Rua 1");
        endereco.setCep("12345-678");
        endereco.setCidade("São Paulo");
        endereco.setUf(testUf);

        when(enderecoRepository.findByCep("12345-678")).thenReturn(Arrays.asList(endereco));

        // When
        boolean result = enderecoService.existeCepParaOutroEndereco("12345-678", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("12345-678");
    }

    @Test
    void testExisteCepParaOutroEndereco_NonExistentCep_ShouldReturnFalse() {
        // Given
        when(enderecoRepository.findByCep("99999-999")).thenReturn(Arrays.asList());

        // When
        boolean result = enderecoService.existeCepParaOutroEndereco("99999-999", 1L);

        // Then
        assertFalse(result);

        // Verify interactions
        verify(enderecoRepository, times(1)).findByCep("99999-999");
    }
}