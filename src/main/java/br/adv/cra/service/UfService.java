package br.adv.cra.service;

import br.adv.cra.dto.UfDTO;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.UfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UfService {
    
    private final UfRepository ufRepository;
    
    public Uf salvar(Uf uf) {
        return ufRepository.save(uf);
    }
    
    public Uf atualizar(Uf uf) {
        if (!ufRepository.existsById(uf.getId())) {
            throw new RuntimeException("UF não encontrada");
        }
        return ufRepository.save(uf);
    }
    
    public void deletar(Long id) {
        if (!ufRepository.existsById(id)) {
            throw new RuntimeException("UF não encontrada");
        }
        ufRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Uf> buscarPorId(Long id) {
        return ufRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<UfDTO> listarTodasDTO() {
        return ufRepository.findAllOrderByNome().stream()
                .map(uf -> new UfDTO(uf.getId(), uf.getSigla(), uf.getNome()))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<Uf> listarTodas() {
        return ufRepository.findAllOrderByNome();
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<Uf> listarTodas(Sort sort) {
        return ufRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public Optional<Uf> buscarPorSigla(String sigla) {
        return ufRepository.findBySigla(sigla.toUpperCase());
    }
    
    @Transactional(readOnly = true)
    public List<Uf> buscarPorNome(String nome) {
        return ufRepository.findByNomeContaining(nome);
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<Uf> buscarPorNome(String nome, Sort sort) {
        return ufRepository.findByNomeContaining(nome, sort);
    }
    
    @Transactional(readOnly = true)
    public boolean existeSigla(String sigla) {
        return ufRepository.existsBySigla(sigla.toUpperCase());
    }
    
    @Transactional(readOnly = true)
    public boolean existeSiglaParaOutraUf(String sigla, Long id) {
        Optional<Uf> uf = ufRepository.findBySigla(sigla.toUpperCase());
        return uf.isPresent() && !uf.get().getId().equals(id);
    }
}