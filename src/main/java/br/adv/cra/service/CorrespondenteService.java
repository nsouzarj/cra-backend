package br.adv.cra.service;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.repository.CorrespondenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CorrespondenteService {
    
    private final CorrespondenteRepository correspondenteRepository;
    private final EnderecoService enderecoService;
    
    public Correspondente salvar(Correspondente correspondente) {
        // Handle endereco saving/updating
        Endereco endereco = correspondente.getEnderecos();
        if (endereco != null) {
            // For new correspondents, always save a new endereco
            endereco = enderecoService.salvar(endereco);
            correspondente.setEnderecos(endereco);
        }
        
        if (correspondente.getDatacadastro() == null) {
            correspondente.setDatacadastro(LocalDateTime.now());
        }
        if (!correspondente.isAtivo()) {
            correspondente.setAtivo(true);
        }
        return correspondenteRepository.save(correspondente);
    }
    
    public Correspondente atualizar(Correspondente correspondente) {
        if (!correspondenteRepository.existsById(correspondente.getId())) {
            throw new RuntimeException("Correspondente não encontrado");
        }
        
        // Handle endereco saving/updating
        Endereco endereco = correspondente.getEnderecos();
        if (endereco != null) {
            // Check if the correspondent already has an address
            Optional<Correspondente> existingCorrespondente = buscarPorId(correspondente.getId());
            if (existingCorrespondente.isPresent() && existingCorrespondente.get().getEnderecos() != null) {
                // Update existing endereco if it exists
                endereco.setId(existingCorrespondente.get().getEnderecos().getId());
                endereco = enderecoService.atualizar(endereco);
            } else {
                // Save new endereco if none exists
                endereco = enderecoService.salvar(endereco);
            }
            correspondente.setEnderecos(endereco);
        }
        
        return correspondenteRepository.save(correspondente);
    }
    
    public void deletar(Long id) {
        if (!correspondenteRepository.existsById(id)) {
            throw new RuntimeException("Correspondente não encontrado");
        }
        correspondenteRepository.deleteById(id);
    }
    
    public void inativar(Long id) {
        Correspondente correspondente = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Correspondente não encontrado"));
        correspondente.setAtivo(false);
        correspondenteRepository.save(correspondente);
    }
    
    public void ativar(Long id) {
        Correspondente correspondente = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Correspondente não encontrado"));
        correspondente.setAtivo(true);
        correspondenteRepository.save(correspondente);
    }
    
    @Transactional(readOnly = true)
    public Optional<Correspondente> buscarPorId(Long id) {
        return correspondenteRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Correspondente> buscarPorIdComSolicitacoes(Long id) {
        return correspondenteRepository.findByIdWithSolicitacoes(id);
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> listarTodos() {
        try {
            return correspondenteRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            throw new RuntimeException("Erro ao buscar todos os correspondentes: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> listarAtivos() {
        return correspondenteRepository.findByAtivoTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> listarInativos() {
        return correspondenteRepository.findByAtivoFalse();
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> buscarPorNome(String nome) {
        return correspondenteRepository.findByNomeContaining(nome);
    }
    
    @Transactional(readOnly = true)
    public Optional<Correspondente> buscarPorCpfCnpj(String cpfCnpj) {
        return correspondenteRepository.findByCpfcnpj(cpfCnpj);
    }
    
    @Transactional(readOnly = true)
    public Optional<Correspondente> buscarPorOab(String oab) {
        return correspondenteRepository.findByOab(oab);
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> buscarPorTipo(String tipo) {
        return correspondenteRepository.findByTipocorrepondente(tipo);
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> buscarPorEmail(String email) {
        return correspondenteRepository.findByAnyEmail(email);
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> listarComRegra1() {
        return correspondenteRepository.findByAplicaregra1True();
    }
    
    @Transactional(readOnly = true)
    public List<Correspondente> listarComRegra2() {
        return correspondenteRepository.findByAplicaregra2True();
    }
    
    @Transactional(readOnly = true)
    public boolean existeCpfCnpj(String cpfCnpj) {
        return correspondenteRepository.existsByCpfcnpj(cpfCnpj);
    }
    
    @Transactional(readOnly = true)
    public boolean existeOab(String oab) {
        return correspondenteRepository.existsByOab(oab);
    }
    
    @Transactional(readOnly = true)
    public boolean existeCpfCnpjParaOutroCorrespondente(String cpfCnpj, Long id) {
        Optional<Correspondente> correspondente = correspondenteRepository.findByCpfcnpj(cpfCnpj);
        return correspondente.isPresent() && !correspondente.get().getId().equals(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existeOabParaOutroCorrespondente(String oab, Long id) {
        Optional<Correspondente> correspondente = correspondenteRepository.findByOab(oab);
        return correspondente.isPresent() && !correspondente.get().getId().equals(id);
    }
}