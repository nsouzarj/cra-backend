package br.adv.cra.service;

import br.adv.cra.entity.Endereco;
import br.adv.cra.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnderecoService {
    
    private final EnderecoRepository enderecoRepository;
    
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }
    
    public Endereco atualizar(Endereco endereco) {
        if (!enderecoRepository.existsById(endereco.getId())) {
            throw new RuntimeException("Endereço não encontrado");
        }
        return enderecoRepository.save(endereco);
    }
    
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado");
        }
        enderecoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Endereco> buscarPorId(Long id) {
        return enderecoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Endereco> listarTodos() {
        return enderecoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Endereco> buscarPorCidade(String cidade) {
        return enderecoRepository.findByCidadeContaining(cidade);
    }
    
    @Transactional(readOnly = true)
    public List<Endereco> buscarPorBairro(String bairro) {
        return enderecoRepository.findByBairroContaining(bairro);
    }
    
    @Transactional(readOnly = true)
    public List<Endereco> buscarPorCep(String cep) {
        return enderecoRepository.findByCep(cep);
    }
    
    @Transactional(readOnly = true)
    public List<Endereco> buscarPorLogradouro(String logradouro) {
        return enderecoRepository.findByLogradouroContaining(logradouro);
    }
}