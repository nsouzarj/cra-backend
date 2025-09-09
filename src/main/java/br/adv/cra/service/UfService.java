package br.adv.cra.service;

import br.adv.cra.entity.Uf;
import br.adv.cra.repository.UfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public List<Uf> listarTodas() {
        return ufRepository.findAllOrderByNome();
    }
    
    @Transactional(readOnly = true)
    public Optional<Uf> buscarPorSigla(String sigla) {
        return ufRepository.findBySigla(sigla.toUpperCase());
    }
    
    @Transactional(readOnly = true)
    public List<Uf> buscarPorNome(String nome) {
        return ufRepository.findByNomeContaining(nome);
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