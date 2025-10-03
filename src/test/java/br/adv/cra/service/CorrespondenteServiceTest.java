package br.adv.cra.service;

import br.adv.cra.dto.CorrespondenteDTO;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.repository.CorrespondenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CorrespondenteService")
class CorrespondenteServiceTest {

    @Mock
    private CorrespondenteRepository correspondenteRepository;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private CorrespondenteService correspondenteService;

    private Correspondente correspondente;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("Cidade Teste");

        correspondente = new Correspondente();
        correspondente.setId(1L);
        correspondente.setNome("Correspondente Teste");
        correspondente.setCpfcnpj("12345678900");
        correspondente.setOab("SP123456");
        correspondente.setAtivo(false); // Inicia como inativo para testar o 'salvar'
        correspondente.setEnderecos(endereco);
    }

    @Test
    @DisplayName("Deve salvar um novo correspondente, seu endereço, e definir como ativo")
    void deveSalvarCorrespondente() {
        when(enderecoService.salvar(any(Endereco.class))).thenReturn(endereco);
        when(correspondenteRepository.save(any(Correspondente.class))).thenAnswer(invocation -> {
            Correspondente c = invocation.getArgument(0);
            c.setId(1L); // Simula a geração de ID pelo banco
            return c;
        });

        Correspondente salvo = correspondenteService.salvar(correspondente);

        assertNotNull(salvo);
        assertNotNull(salvo.getDatacadastro());
        assertTrue(salvo.isAtivo());
        assertEquals(endereco, salvo.getEnderecos());
        verify(enderecoService, times(1)).salvar(endereco);
        verify(correspondenteRepository, times(1)).save(correspondente);
    }

    @Test
    @DisplayName("Deve atualizar um correspondente e seu endereço existente")
    void deveAtualizarCorrespondenteComEnderecoExistente() {
        Correspondente correspondenteAtualizacao = new Correspondente();
        correspondenteAtualizacao.setId(1L);
        correspondenteAtualizacao.setNome("Nome Atualizado");
        
        Endereco enderecoAtualizacao = new Endereco();
        enderecoAtualizacao.setCidade("Cidade Nova");
        correspondenteAtualizacao.setEnderecos(enderecoAtualizacao);

        // O correspondente existente no banco já tem um endereço
        correspondente.setAtivo(true);
        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));
        when(enderecoService.atualizar(any(Endereco.class))).thenReturn(enderecoAtualizacao);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(correspondenteAtualizacao);

        Correspondente atualizado = correspondenteService.atualizar(correspondenteAtualizacao);

        assertNotNull(atualizado);
        assertEquals("Nome Atualizado", atualizado.getNome());
        assertEquals("Cidade Nova", atualizado.getEnderecos().getCidade());
        verify(enderecoService, times(1)).atualizar(any(Endereco.class));
        verify(enderecoService, never()).salvar(any(Endereco.class));
        verify(correspondenteRepository, times(1)).save(correspondenteAtualizacao);
    }

    @Test
    @DisplayName("Deve atualizar um correspondente e salvar um novo endereço se não existir um")
    void deveAtualizarCorrespondenteESalvarNovoEndereco() {
        correspondente.setEnderecos(null); // Correspondente no banco não tem endereço

        Correspondente correspondenteAtualizacao = new Correspondente();
        correspondenteAtualizacao.setId(1L);
        correspondenteAtualizacao.setEnderecos(endereco);

        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));
        when(enderecoService.salvar(any(Endereco.class))).thenReturn(endereco);
        when(correspondenteRepository.save(any(Correspondente.class))).thenReturn(correspondenteAtualizacao);

        Correspondente atualizado = correspondenteService.atualizar(correspondenteAtualizacao);

        assertNotNull(atualizado.getEnderecos());
        verify(enderecoService, times(1)).salvar(any(Endereco.class));
        verify(enderecoService, never()).atualizar(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar correspondente inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(correspondenteRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.atualizar(correspondente);
        });

        assertEquals("Correspondente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um correspondente existente")
    void deveDeletarCorrespondente() {
        when(correspondenteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(correspondenteRepository).deleteById(1L);

        assertDoesNotThrow(() -> correspondenteService.deletar(1L));

        verify(correspondenteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar correspondente inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(correspondenteRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.deletar(1L);
        });

        assertEquals("Correspondente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve inativar um correspondente")
    void deveInativarCorrespondente() {
        correspondente.setAtivo(true);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));

        correspondenteService.inativar(1L);

        assertFalse(correspondente.isAtivo());
        verify(correspondenteRepository, times(1)).save(correspondente);
    }

    @Test
    @DisplayName("Deve ativar um correspondente")
    void deveAtivarCorrespondente() {
        correspondente.setAtivo(false);
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));

        correspondenteService.ativar(1L);

        assertTrue(correspondente.isAtivo());
        verify(correspondenteRepository, times(1)).save(correspondente);
    }

    @Test
    @DisplayName("Deve buscar um correspondente por ID")
    void deveBuscarPorId() {
        when(correspondenteRepository.findById(1L)).thenReturn(Optional.of(correspondente));
        Optional<Correspondente> encontrado = correspondenteService.buscarPorId(1L);
        assertTrue(encontrado.isPresent());
        assertEquals(correspondente, encontrado.get());
    }

    @Test
    @DisplayName("Deve listar todos os correspondentes como DTO")
    void deveListarTodosDTO() {
        when(correspondenteRepository.findAll()).thenReturn(Collections.singletonList(correspondente));

        List<CorrespondenteDTO> dtos = correspondenteService.listarTodosDTO();

        assertFalse(dtos.isEmpty());
        assertEquals(1, dtos.size());
        assertEquals("Correspondente Teste", dtos.get(0).getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar DTOs se o repositório falhar")
    void deveLancarExcecaoAoListarDTOsComErro() {
        when(correspondenteRepository.findAll()).thenThrow(new RuntimeException("Erro de banco"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            correspondenteService.listarTodosDTO();
        });

        assertTrue(exception.getMessage().contains("Erro ao buscar todos os correspondentes DTO"));
    }

    @Test
    @DisplayName("Deve listar todos os correspondentes")
    void deveListarTodos() {
        when(correspondenteRepository.findAll()).thenReturn(Collections.singletonList(correspondente));
        List<Correspondente> lista = correspondenteService.listarTodos();
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve listar correspondentes ativos")
    void deveListarAtivos() {
        correspondente.setAtivo(true);
        when(correspondenteRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(correspondente));
        List<Correspondente> lista = correspondenteService.listarAtivos();
        assertFalse(lista.isEmpty());
        assertTrue(lista.get(0).isAtivo());
    }

    @Test
    @DisplayName("Deve buscar por CPF/CNPJ")
    void deveBuscarPorCpfCnpj() {
        when(correspondenteRepository.findByCpfcnpj("12345678900")).thenReturn(Optional.of(correspondente));
        Optional<Correspondente> encontrado = correspondenteService.buscarPorCpfCnpj("12345678900");
        assertTrue(encontrado.isPresent());
        assertEquals("12345678900", encontrado.get().getCpfcnpj());
    }

    @Test
    @DisplayName("Deve retornar true se CPF/CNPJ existe para outro correspondente")
    void deveRetornarTrueSeCpfCnpjExisteParaOutro() {
        Correspondente outro = new Correspondente();
        outro.setId(2L);
        outro.setCpfcnpj("12345678900");

        when(correspondenteRepository.findByCpfcnpj("12345678900")).thenReturn(Optional.of(outro));

        assertTrue(correspondenteService.existeCpfCnpjParaOutroCorrespondente("12345678900", 1L));
    }

    @Test
    @DisplayName("Deve retornar false se CPF/CNPJ existe para o mesmo correspondente")
    void deveRetornarFalseSeCpfCnpjExisteParaOMesmo() {
        when(correspondenteRepository.findByCpfcnpj("12345678900")).thenReturn(Optional.of(correspondente));
        assertFalse(correspondenteService.existeCpfCnpjParaOutroCorrespondente("12345678900", 1L));
    }

    @Test
    @DisplayName("Deve retornar true se OAB existe")
    void deveRetornarTrueSeOabExiste() {
        when(correspondenteRepository.existsByOab("SP123456")).thenReturn(true);
        assertTrue(correspondenteService.existeOab("SP123456"));
    }

    @Test
    @DisplayName("Deve retornar false se OAB não existe")
    void deveRetornarFalseSeOabNaoExiste() {
        when(correspondenteRepository.existsByOab("RJ987654")).thenReturn(false);
        assertFalse(correspondenteService.existeOab("RJ987654"));
    }
}