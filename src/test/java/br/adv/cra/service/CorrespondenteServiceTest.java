package br.adv.cra.service;

import br.adv.cra.dto.CorrespondenteDTO;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.CorrespondenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CorrespondenteServiceTest {

    @Mock
    private CorrespondenteRepository correspondenteRepository;

    @Mock
    private EnderecoService enderecoService;

    private CorrespondenteService correspondenteService;

    private Correspondente testCorrespondente;
    private Endereco testEndereco;
    private Uf testUf;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        correspondenteService = new CorrespondenteService(correspondenteRepository, enderecoService);

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
        testEndereco.setCep("01000-000");

        // Create test correspondente
        testCorrespondente = new Correspondente();
        testCorrespondente.setId(1L);
        testCorrespondente.setNome("Correspondente Teste");
        testCorrespondente.setCpfcnpj("12345678901");
        testCorrespondente.setOab("123456");
        testCorrespondente.setEmailprimario("teste@example.com");
        testCorrespondente.setTelefoneprimario("(11) 99999-9999");
        testCorrespondente.setTipocorrepondente("Advogado");
        testCorrespondente.setDatacadastro(LocalDateTime.now());
        testCorrespondente.setAtivo(true);
        testCorrespondente.setAplicaregra1(true);
        testCorrespondente.setAplicaregra2(false);
        testCorrespondente.setEnderecos(testEndereco);
    }

    @Test
    void testSalvar_NewCorrespondenteWithoutEndereco_ShouldSaveWithCurrentDateAndActive() {
        // Given
        Correspondente newCorrespondente = new Correspondente();
        newCorrespondente.setNome("Novo Correspondente");
        newCorrespondente.setCpfcnpj("98765432109");
        newCorrespondente.setOab("654321");
        newCorrespondente.setEmailprimario("novo@example.com");
        newCorrespondente.setTelefoneprimario("(11) 88888-8888");
        newCorrespondente.setTipocorrepondente("Advogado");
        // No endereco
        // datacadastro is null, should be set to current date
        newCorrespondente.setAtivo(false); // Should be set to true

        Correspondente savedCorrespondente = new Correspondente();
        savedCorrespondente.setId(2L);
        savedCorrespondente.setNome("Novo Correspondente");
        savedCorrespondente.setCpfcnpj("98765432109");
        savedCorrespondente.setOab("654321");
        savedCorrespondente.setEmailprimario("novo@example.com");
        savedCorrespondente.setTelefoneprimario("(11) 88888-8888");
        savedCorrespondente.setTipocorrepondente("Advogado");
        savedCorrespondente.setDatacadastro(LocalDateTime.now());
        savedCorrespondente.setAtivo(true); // Should be set to true

        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(savedCorrespondente);

        // When
        Correspondente result = correspondenteService.salvar(newCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals("Novo Correspondente", result.getNome());
        assertNotNull(result.getDatacadastro());
        assertTrue(result.isAtivo());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).save(newCorrespondente);
        verify(enderecoService, never()).salvar(any(Endereco.class));
    }

    @Test
    void testSalvar_NewCorrespondenteWithEndereco_ShouldSaveEnderecoAndCorrespondente() {
        // Given
        Endereco newEndereco = new Endereco();
        newEndereco.setLogradouro("Av. Nova");
        newEndereco.setNumero("456");
        newEndereco.setBairro("Novo Bairro");
        newEndereco.setCidade("Campinas");
        newEndereco.setUf(testUf);
        newEndereco.setCep("13000-000");

        Correspondente newCorrespondente = new Correspondente();
        newCorrespondente.setNome("Novo Correspondente");
        newCorrespondente.setCpfcnpj("98765432109");
        newCorrespondente.setOab("654321");
        newCorrespondente.setEmailprimario("novo@example.com");
        newCorrespondente.setTelefoneprimario("(11) 88888-8888");
        newCorrespondente.setTipocorrepondente("Advogado");
        newCorrespondente.setEnderecos(newEndereco);
        newCorrespondente.setDatacadastro(null); // Should be set to current date

        Endereco savedEndereco = new Endereco();
        savedEndereco.setId(2L);
        savedEndereco.setLogradouro("Av. Nova");
        savedEndereco.setNumero("456");
        savedEndereco.setBairro("Novo Bairro");
        savedEndereco.setCidade("Campinas");
        savedEndereco.setUf(testUf);
        savedEndereco.setCep("13000-000");

        Correspondente savedCorrespondente = new Correspondente();
        savedCorrespondente.setId(2L);
        savedCorrespondente.setNome("Novo Correspondente");
        savedCorrespondente.setCpfcnpj("98765432109");
        savedCorrespondente.setOab("654321");
        savedCorrespondente.setEmailprimario("novo@example.com");
        savedCorrespondente.setTelefoneprimario("(11) 88888-8888");
        savedCorrespondente.setTipocorrepondente("Advogado");
        savedCorrespondente.setEnderecos(savedEndereco);
        savedCorrespondente.setDatacadastro(LocalDateTime.now());
        savedCorrespondente.setAtivo(true);

        when(enderecoService.salvar(newEndereco)).thenReturn(savedEndereco);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(savedCorrespondente);

        // When
        Correspondente result = correspondenteService.salvar(newCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals("Novo Correspondente", result.getNome());
        assertNotNull(result.getEnderecos());
        assertEquals(2L, result.getEnderecos().getId());
        assertNotNull(result.getDatacadastro());
        assertTrue(result.isAtivo());
        
        // Verify interactions
        verify(enderecoService, times(1)).salvar(newEndereco);
        verify(correspondenteRepository, times(1)).save(newCorrespondente);
    }

    @Test
    void testAtualizar_ExistingCorrespondenteWithoutEndereco_ShouldUpdateCorrespondente() {
        // Given
        Correspondente updatedCorrespondente = new Correspondente();
        updatedCorrespondente.setId(1L);
        updatedCorrespondente.setNome("Correspondente Atualizado");
        updatedCorrespondente.setCpfcnpj("12345678901");
        updatedCorrespondente.setOab("123456");
        updatedCorrespondente.setEmailprimario("atualizado@example.com");
        updatedCorrespondente.setTelefoneprimario("(11) 77777-7777");
        updatedCorrespondente.setTipocorrepondente("Advogado");
        // No endereco
        updatedCorrespondente.setDatacadastro(null); // Should be set to current date

        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(updatedCorrespondente);

        // When
        Correspondente result = correspondenteService.atualizar(updatedCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals("Correspondente Atualizado", result.getNome());
        assertEquals("atualizado@example.com", result.getEmailprimario());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(1L);
        verify(correspondenteRepository, times(1)).save(updatedCorrespondente);
        verify(correspondenteRepository, never()).findById(1L);
        verify(enderecoService, never()).atualizar(any(Endereco.class));
        verify(enderecoService, never()).salvar(any(Endereco.class));
    }

    @Test
    void testAtualizar_ExistingCorrespondenteWithExistingEndereco_ShouldUpdateEndereco() {
        // Given
        Endereco updatedEndereco = new Endereco();
        updatedEndereco.setLogradouro("Rua Atualizada");
        updatedEndereco.setNumero("321");
        updatedEndereco.setBairro("Bairro Atualizado");
        updatedEndereco.setCidade("São Paulo");
        updatedEndereco.setUf(testUf);
        updatedEndereco.setCep("01000-001");

        Correspondente updatedCorrespondente = new Correspondente();
        updatedCorrespondente.setId(1L);
        updatedCorrespondente.setNome("Correspondente Atualizado");
        updatedCorrespondente.setCpfcnpj("12345678901");
        updatedCorrespondente.setOab("123456");
        updatedCorrespondente.setEmailprimario("atualizado@example.com");
        updatedCorrespondente.setTelefoneprimario("(11) 77777-7777");
        updatedCorrespondente.setTipocorrepondente("Advogado");
        updatedCorrespondente.setEnderecos(updatedEndereco);
        updatedCorrespondente.setDatacadastro(null); // Should be set to current date

        Endereco savedEndereco = new Endereco();
        savedEndereco.setId(1L); // Same ID as existing endereco
        savedEndereco.setLogradouro("Rua Atualizada");
        savedEndereco.setNumero("321");
        savedEndereco.setBairro("Bairro Atualizado");
        savedEndereco.setCidade("São Paulo");
        savedEndereco.setUf(testUf);
        savedEndereco.setCep("01000-001");

        Correspondente savedCorrespondente = new Correspondente();
        savedCorrespondente.setId(1L);
        savedCorrespondente.setNome("Correspondente Atualizado");
        savedCorrespondente.setCpfcnpj("12345678901");
        savedCorrespondente.setOab("123456");
        savedCorrespondente.setEmailprimario("atualizado@example.com");
        savedCorrespondente.setTelefoneprimario("(11) 77777-7777");
        savedCorrespondente.setTipocorrepondente("Advogado");
        savedCorrespondente.setEnderecos(savedEndereco);
        savedCorrespondente.setDatacadastro(LocalDateTime.now());

        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(testCorrespondente));
        when(enderecoService.atualizar(updatedEndereco)).thenReturn(savedEndereco);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(savedCorrespondente);

        // When
        Correspondente result = correspondenteService.atualizar(updatedCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals("Correspondente Atualizado", result.getNome());
        assertNotNull(result.getEnderecos());
        assertEquals(1L, result.getEnderecos().getId());
        assertEquals("Rua Atualizada", result.getEnderecos().getLogradouro());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(1L);
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(enderecoService, times(1)).atualizar(updatedEndereco);
        verify(correspondenteRepository, times(1)).save(updatedCorrespondente);
    }

    @Test
    void testAtualizar_ExistingCorrespondenteWithoutExistingEndereco_ShouldSaveNewEndereco() {
        // Given
        Correspondente existingCorrespondente = new Correspondente();
        existingCorrespondente.setId(1L);
        existingCorrespondente.setNome("Correspondente Teste");
        existingCorrespondente.setCpfcnpj("12345678901");
        existingCorrespondente.setOab("123456");
        existingCorrespondente.setEmailprimario("teste@example.com");
        existingCorrespondente.setTelefoneprimario("(11) 99999-9999");
        existingCorrespondente.setTipocorrepondente("Advogado");
        existingCorrespondente.setDatacadastro(LocalDateTime.now());
        existingCorrespondente.setAtivo(true);
        existingCorrespondente.setAplicaregra1(true);
        existingCorrespondente.setAplicaregra2(false);
        // No existing endereco

        Endereco newEndereco = new Endereco();
        newEndereco.setLogradouro("Rua Nova");
        newEndereco.setNumero("789");
        newEndereco.setBairro("Novo Bairro");
        newEndereco.setCidade("São Paulo");
        newEndereco.setUf(testUf);
        newEndereco.setCep("01000-002");

        Correspondente updatedCorrespondente = new Correspondente();
        updatedCorrespondente.setId(1L);
        updatedCorrespondente.setNome("Correspondente Atualizado");
        updatedCorrespondente.setCpfcnpj("12345678901");
        updatedCorrespondente.setOab("123456");
        updatedCorrespondente.setEmailprimario("atualizado@example.com");
        updatedCorrespondente.setTelefoneprimario("(11) 77777-7777");
        updatedCorrespondente.setTipocorrepondente("Advogado");
        updatedCorrespondente.setEnderecos(newEndereco);
        updatedCorrespondente.setDatacadastro(null); // Should be set to current date

        Endereco savedEndereco = new Endereco();
        savedEndereco.setId(3L); // New ID
        savedEndereco.setLogradouro("Rua Nova");
        savedEndereco.setNumero("789");
        savedEndereco.setBairro("Novo Bairro");
        savedEndereco.setCidade("São Paulo");
        savedEndereco.setUf(testUf);
        savedEndereco.setCep("01000-002");

        Correspondente savedCorrespondente = new Correspondente();
        savedCorrespondente.setId(1L);
        savedCorrespondente.setNome("Correspondente Atualizado");
        savedCorrespondente.setCpfcnpj("12345678901");
        savedCorrespondente.setOab("123456");
        savedCorrespondente.setEmailprimario("atualizado@example.com");
        savedCorrespondente.setTelefoneprimario("(11) 77777-7777");
        savedCorrespondente.setTipocorrepondente("Advogado");
        savedCorrespondente.setEnderecos(savedEndereco);
        savedCorrespondente.setDatacadastro(LocalDateTime.now());

        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(existingCorrespondente));
        when(enderecoService.salvar(newEndereco)).thenReturn(savedEndereco);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(savedCorrespondente);

        // When
        Correspondente result = correspondenteService.atualizar(updatedCorrespondente);

        // Then
        assertNotNull(result);
        assertEquals("Correspondente Atualizado", result.getNome());
        assertNotNull(result.getEnderecos());
        assertEquals(3L, result.getEnderecos().getId());
        assertEquals("Rua Nova", result.getEnderecos().getLogradouro());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(1L);
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(enderecoService, times(1)).salvar(newEndereco);
        verify(correspondenteRepository, times(1)).save(updatedCorrespondente);
    }

    @Test
    void testAtualizar_NonExistentCorrespondente_ShouldThrowException() {
        // Given
        Correspondente updatedCorrespondente = new Correspondente();
        updatedCorrespondente.setId(999L);
        updatedCorrespondente.setNome("Non Existent");

        when(correspondenteRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.atualizar(updatedCorrespondente);
        });
        
        assertEquals("Correspondente não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(999L);
        verify(correspondenteRepository, never()).findById(anyLong());
        verify(correspondenteRepository, never()).save(any(Correspondente.class));
        verify(enderecoService, never()).atualizar(any(Endereco.class));
        verify(enderecoService, never()).salvar(any(Endereco.class));
    }

    @Test
    void testDeletar_ExistingCorrespondente_ShouldDeleteCorrespondente() {
        // Given
        when(correspondenteRepository.existsById(1L)).thenReturn(true);

        // When
        correspondenteService.deletar(1L);

        // Then
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(1L);
        verify(correspondenteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_NonExistentCorrespondente_ShouldThrowException() {
        // Given
        when(correspondenteRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.deletar(999L);
        });
        
        assertEquals("Correspondente não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsById(999L);
        verify(correspondenteRepository, never()).deleteById(anyLong());
    }

    @Test
    void testInativar_ExistingCorrespondente_ShouldSetCorrespondenteInactive() {
        // Given
        Correspondente activeCorrespondente = new Correspondente();
        activeCorrespondente.setId(1L);
        activeCorrespondente.setNome("Correspondente Teste");
        activeCorrespondente.setCpfcnpj("12345678901");
        activeCorrespondente.setOab("123456");
        activeCorrespondente.setEmailprimario("teste@example.com");
        activeCorrespondente.setAtivo(true); // Initially active

        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(activeCorrespondente));
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(activeCorrespondente);

        // When
        correspondenteService.inativar(1L);

        // Then
        assertFalse(activeCorrespondente.isAtivo()); // Should be inactive now
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(correspondenteRepository, times(1)).save(activeCorrespondente);
    }

    @Test
    void testInativar_NonExistentCorrespondente_ShouldThrowException() {
        // Given
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.inativar(999L);
        });
        
        assertEquals("Correspondente não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(999L);
        verify(correspondenteRepository, never()).save(any(Correspondente.class));
    }

    @Test
    void testAtivar_ExistingCorrespondente_ShouldSetCorrespondenteActive() {
        // Given
        Correspondente inactiveCorrespondente = new Correspondente();
        inactiveCorrespondente.setId(1L);
        inactiveCorrespondente.setNome("Correspondente Teste");
        inactiveCorrespondente.setCpfcnpj("12345678901");
        inactiveCorrespondente.setOab("123456");
        inactiveCorrespondente.setEmailprimario("teste@example.com");
        inactiveCorrespondente.setAtivo(false); // Initially inactive

        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(inactiveCorrespondente));
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(inactiveCorrespondente);

        // When
        correspondenteService.ativar(1L);

        // Then
        assertTrue(inactiveCorrespondente.isAtivo()); // Should be active now
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(1L);
        verify(correspondenteRepository, times(1)).save(inactiveCorrespondente);
    }

    @Test
    void testAtivar_NonExistentCorrespondente_ShouldThrowException() {
        // Given
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.ativar(999L);
        });
        
        assertEquals("Correspondente não encontrado", exception.getMessage());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(999L);
        verify(correspondenteRepository, never()).save(any(Correspondente.class));
    }

    @Test
    void testBuscarPorId_ExistingCorrespondente_ShouldReturnCorrespondente() {
        // Given
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(testCorrespondente));

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCorrespondente, result.get());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NonExistentCorrespondente_ShouldReturnEmpty() {
        // Given
        when(correspondenteRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorId(999L);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findById(999L);
    }

    @Test
    void testBuscarPorIdComSolicitacoes_ExistingCorrespondente_ShouldReturnCorrespondente() {
        // Given
        when(correspondenteRepository.findByIdWithSolicitacoes(1L)).thenReturn(Optional.of(testCorrespondente));

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorIdComSolicitacoes(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCorrespondente, result.get());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByIdWithSolicitacoes(1L);
    }

    @Test
    void testBuscarPorIdComSolicitacoes_NonExistentCorrespondente_ShouldReturnEmpty() {
        // Given
        when(correspondenteRepository.findByIdWithSolicitacoes(999L)).thenReturn(Optional.empty());

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorIdComSolicitacoes(999L);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByIdWithSolicitacoes(999L);
    }

    @Test
    void testListarTodosDTO_ShouldReturnAllCorrespondentesAsDTOs() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setOab("111111");
        correspondente1.setEmailprimario("um@example.com");
        correspondente1.setTelefoneprimario("(11) 11111-1111");
        correspondente1.setTipocorrepondente("Advogado");
        correspondente1.setDatacadastro(LocalDateTime.now());
        correspondente1.setAtivo(true);
        correspondente1.setAplicaregra1(true);
        correspondente1.setAplicaregra2(false);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setOab("222222");
        correspondente2.setEmailprimario("dois@example.com");
        correspondente2.setTelefoneprimario("(11) 22222-2222");
        correspondente2.setTipocorrepondente("Contador");
        correspondente2.setDatacadastro(LocalDateTime.now());
        correspondente2.setAtivo(false);
        correspondente2.setAplicaregra1(false);
        correspondente2.setAplicaregra2(true);

        when(correspondenteRepository.findAll()).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<CorrespondenteDTO> result = correspondenteService.listarTodosDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Correspondente Um", result.get(0).getNome());
        assertEquals("Correspondente Dois", result.get(1).getNome());
        assertEquals("11111111111", result.get(0).getCpfcnpj());
        assertEquals("22222222222", result.get(1).getCpfcnpj());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findAll();
    }

    @Test
    void testListarTodos_ShouldReturnAllCorrespondentes() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setOab("111111");
        correspondente1.setEmailprimario("um@example.com");
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setOab("222222");
        correspondente2.setEmailprimario("dois@example.com");
        correspondente2.setAtivo(false);

        when(correspondenteRepository.findAll()).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Correspondente Um", result.get(0).getNome());
        assertEquals("Correspondente Dois", result.get(1).getNome());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findAll();
    }

    @Test
    void testListarAtivos_ShouldReturnOnlyActiveCorrespondentes() {
        // Given
        Correspondente activeCorrespondente1 = new Correspondente();
        activeCorrespondente1.setId(1L);
        activeCorrespondente1.setNome("Correspondente Um");
        activeCorrespondente1.setCpfcnpj("11111111111");
        activeCorrespondente1.setAtivo(true);

        Correspondente activeCorrespondente2 = new Correspondente();
        activeCorrespondente2.setId(2L);
        activeCorrespondente2.setNome("Correspondente Dois");
        activeCorrespondente2.setCpfcnpj("22222222222");
        activeCorrespondente2.setAtivo(true);

        when(correspondenteRepository.findByAtivoTrue()).thenReturn(List.of(activeCorrespondente1, activeCorrespondente2));

        // When
        List<Correspondente> result = correspondenteService.listarAtivos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isAtivo());
        assertTrue(result.get(1).isAtivo());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByAtivoTrue();
    }

    @Test
    void testListarInativos_ShouldReturnOnlyInactiveCorrespondentes() {
        // Given
        Correspondente inactiveCorrespondente1 = new Correspondente();
        inactiveCorrespondente1.setId(1L);
        inactiveCorrespondente1.setNome("Correspondente Um");
        inactiveCorrespondente1.setCpfcnpj("11111111111");
        inactiveCorrespondente1.setAtivo(false);

        Correspondente inactiveCorrespondente2 = new Correspondente();
        inactiveCorrespondente2.setId(2L);
        inactiveCorrespondente2.setNome("Correspondente Dois");
        inactiveCorrespondente2.setCpfcnpj("22222222222");
        inactiveCorrespondente2.setAtivo(false);

        when(correspondenteRepository.findByAtivoFalse()).thenReturn(List.of(inactiveCorrespondente1, inactiveCorrespondente2));

        // When
        List<Correspondente> result = correspondenteService.listarInativos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.get(0).isAtivo());
        assertFalse(result.get(1).isAtivo());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByAtivoFalse();
    }

    @Test
    void testBuscarPorNome_ShouldReturnCorrespondentesWithMatchingName() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("João Silva");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Maria Silva");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setAtivo(true);

        when(correspondenteRepository.findByNomeContaining("Silva")).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.buscarPorNome("Silva");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getNome().contains("Silva"));
        assertTrue(result.get(1).getNome().contains("Silva"));
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByNomeContaining("Silva");
    }

    @Test
    void testBuscarPorCpfCnpj_ExistingCpfCnpj_ShouldReturnCorrespondente() {
        // Given
        when(correspondenteRepository.findByCpfcnpj("12345678901")).thenReturn(Optional.of(testCorrespondente));

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorCpfCnpj("12345678901");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCorrespondente, result.get());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByCpfcnpj("12345678901");
    }

    @Test
    void testBuscarPorCpfCnpj_NonExistentCpfCnpj_ShouldReturnEmpty() {
        // Given
        when(correspondenteRepository.findByCpfcnpj("99999999999")).thenReturn(Optional.empty());

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorCpfCnpj("99999999999");

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByCpfcnpj("99999999999");
    }

    @Test
    void testBuscarPorOab_ExistingOab_ShouldReturnCorrespondente() {
        // Given
        when(correspondenteRepository.findByOab("123456")).thenReturn(Optional.of(testCorrespondente));

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorOab("123456");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCorrespondente, result.get());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByOab("123456");
    }

    @Test
    void testBuscarPorOab_NonExistentOab_ShouldReturnEmpty() {
        // Given
        when(correspondenteRepository.findByOab("999999")).thenReturn(Optional.empty());

        // When
        Optional<Correspondente> result = correspondenteService.buscarPorOab("999999");

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByOab("999999");
    }

    @Test
    void testBuscarPorTipo_ShouldReturnCorrespondentesWithSpecificType() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setTipocorrepondente("Advogado");
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setTipocorrepondente("Advogado");
        correspondente2.setAtivo(true);

        when(correspondenteRepository.findByTipocorrepondente("Advogado")).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.buscarPorTipo("Advogado");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Advogado", result.get(0).getTipocorrepondente());
        assertEquals("Advogado", result.get(1).getTipocorrepondente());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByTipocorrepondente("Advogado");
    }

    @Test
    void testBuscarPorEmail_ShouldReturnCorrespondentesWithMatchingEmail() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setEmailprimario("um@example.com");
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setEmailprimario("dois@example.com");
        correspondente2.setAtivo(true);

        when(correspondenteRepository.findByAnyEmail("example.com")).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.buscarPorEmail("example.com");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getEmailprimario().contains("example.com"));
        assertTrue(result.get(1).getEmailprimario().contains("example.com"));
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByAnyEmail("example.com");
    }

    @Test
    void testListarComRegra1_ShouldReturnCorrespondentesWithRegra1() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setAplicaregra1(true);
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setAplicaregra1(true);
        correspondente2.setAtivo(true);

        when(correspondenteRepository.findByAplicaregra1True()).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.listarComRegra1();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isAplicaregra1());
        assertTrue(result.get(1).isAplicaregra1());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByAplicaregra1True();
    }

    @Test
    void testListarComRegra2_ShouldReturnCorrespondentesWithRegra2() {
        // Given
        Correspondente correspondente1 = new Correspondente();
        correspondente1.setId(1L);
        correspondente1.setNome("Correspondente Um");
        correspondente1.setCpfcnpj("11111111111");
        correspondente1.setAplicaregra2(true);
        correspondente1.setAtivo(true);

        Correspondente correspondente2 = new Correspondente();
        correspondente2.setId(2L);
        correspondente2.setNome("Correspondente Dois");
        correspondente2.setCpfcnpj("22222222222");
        correspondente2.setAplicaregra2(true);
        correspondente2.setAtivo(true);

        when(correspondenteRepository.findByAplicaregra2True()).thenReturn(List.of(correspondente1, correspondente2));

        // When
        List<Correspondente> result = correspondenteService.listarComRegra2();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isAplicaregra2());
        assertTrue(result.get(1).isAplicaregra2());
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByAplicaregra2True();
    }

    @Test
    void testExisteCpfCnpj_ExistingCpfCnpj_ShouldReturnTrue() {
        // Given
        when(correspondenteRepository.existsByCpfcnpj("12345678901")).thenReturn(true);

        // When
        boolean result = correspondenteService.existeCpfCnpj("12345678901");

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsByCpfcnpj("12345678901");
    }

    @Test
    void testExisteCpfCnpj_NonExistentCpfCnpj_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.existsByCpfcnpj("99999999999")).thenReturn(false);

        // When
        boolean result = correspondenteService.existeCpfCnpj("99999999999");

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsByCpfcnpj("99999999999");
    }

    @Test
    void testExisteOab_ExistingOab_ShouldReturnTrue() {
        // Given
        when(correspondenteRepository.existsByOab("123456")).thenReturn(true);

        // When
        boolean result = correspondenteService.existeOab("123456");

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsByOab("123456");
    }

    @Test
    void testExisteOab_NonExistentOab_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.existsByOab("999999")).thenReturn(false);

        // When
        boolean result = correspondenteService.existeOab("999999");

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).existsByOab("999999");
    }

    @Test
    void testExisteCpfCnpjParaOutroCorrespondente_SameCorrespondente_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.findByCpfcnpj("12345678901")).thenReturn(Optional.of(testCorrespondente));

        // When
        boolean result = correspondenteService.existeCpfCnpjParaOutroCorrespondente("12345678901", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByCpfcnpj("12345678901");
    }

    @Test
    void testExisteCpfCnpjParaOutroCorrespondente_DifferentCorrespondente_ShouldReturnTrue() {
        // Given
        Correspondente otherCorrespondente = new Correspondente();
        otherCorrespondente.setId(2L);
        otherCorrespondente.setNome("Outro Correspondente");
        otherCorrespondente.setCpfcnpj("12345678901");

        when(correspondenteRepository.findByCpfcnpj("12345678901")).thenReturn(Optional.of(otherCorrespondente));

        // When
        boolean result = correspondenteService.existeCpfCnpjParaOutroCorrespondente("12345678901", 1L);

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByCpfcnpj("12345678901");
    }

    @Test
    void testExisteCpfCnpjParaOutroCorrespondente_NonExistentCpfCnpj_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.findByCpfcnpj("99999999999")).thenReturn(Optional.empty());

        // When
        boolean result = correspondenteService.existeCpfCnpjParaOutroCorrespondente("99999999999", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByCpfcnpj("99999999999");
    }

    @Test
    void testExisteOabParaOutroCorrespondente_SameCorrespondente_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.findByOab("123456")).thenReturn(Optional.of(testCorrespondente));

        // When
        boolean result = correspondenteService.existeOabParaOutroCorrespondente("123456", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByOab("123456");
    }

    @Test
    void testExisteOabParaOutroCorrespondente_DifferentCorrespondente_ShouldReturnTrue() {
        // Given
        Correspondente otherCorrespondente = new Correspondente();
        otherCorrespondente.setId(2L);
        otherCorrespondente.setNome("Outro Correspondente");
        otherCorrespondente.setOab("123456");

        when(correspondenteRepository.findByOab("123456")).thenReturn(Optional.of(otherCorrespondente));

        // When
        boolean result = correspondenteService.existeOabParaOutroCorrespondente("123456", 1L);

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByOab("123456");
    }

    @Test
    void testExisteOabParaOutroCorrespondente_NonExistentOab_ShouldReturnFalse() {
        // Given
        when(correspondenteRepository.findByOab("999999")).thenReturn(Optional.empty());

        // When
        boolean result = correspondenteService.existeOabParaOutroCorrespondente("999999", 1L);

        // Then
        assertFalse(result);
        
        // Verify interactions
        verify(correspondenteRepository, times(1)).findByOab("999999");
    }
}