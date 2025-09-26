package br.adv.cra.service;

import br.adv.cra.dto.TipoSolicitacaoDTO;
import br.adv.cra.entity.TipoSolicitacao;
import br.adv.cra.repository.TipoSolicitacaoRepository;
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
public class TipoSolicitacaoService {
    
    private final TipoSolicitacaoRepository tipoSolicitacaoRepository;
    
    public TipoSolicitacao salvar(TipoSolicitacao tipoSolicitacao) {
        return tipoSolicitacaoRepository.save(tipoSolicitacao);
    }
    
    public TipoSolicitacao atualizar(TipoSolicitacao tipoSolicitacao) {
        if (!tipoSolicitacaoRepository.existsById(tipoSolicitacao.getIdtiposolicitacao())) {
            throw new RuntimeException("Tipo de Solicitação não encontrado");
        }
        return tipoSolicitacaoRepository.save(tipoSolicitacao);
    }
    
    public void deletar(Long id) {
        if (!tipoSolicitacaoRepository.existsById(id)) {
            throw new RuntimeException("Tipo de Solicitação não encontrado");
        }
        tipoSolicitacaoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<TipoSolicitacao> buscarPorId(Long id) {
        return tipoSolicitacaoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacaoDTO> listarTodosDTO() {
        return tipoSolicitacaoRepository.findAll().stream()
                .map(tipo -> new TipoSolicitacaoDTO(
                    tipo.getIdtiposolicitacao(),
                    tipo.getEspecie(),
                    tipo.getDescricao(),
                    tipo.getTipo(),
                    tipo.getVisualizar()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTodos() {
        return tipoSolicitacaoRepository.findAll();
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTodos(Sort sort) {
        return tipoSolicitacaoRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTodosOrdenados() {
        return tipoSolicitacaoRepository.findAllOrderByEspecie();
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTodosOrdenados(Sort sort) {
        return tipoSolicitacaoRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorEspecie(String especie) {
        return tipoSolicitacaoRepository.findByEspecieContaining(especie);
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorEspecie(String especie, Sort sort) {
        return tipoSolicitacaoRepository.findByEspecieContaining(especie, sort);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorDescricao(String descricao) {
        return tipoSolicitacaoRepository.findByDescricaoContaining(descricao);
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorDescricao(String descricao, Sort sort) {
        return tipoSolicitacaoRepository.findByDescricaoContaining(descricao, sort);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorTipo(String tipo) {
        return tipoSolicitacaoRepository.findByTipo(tipo);
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorTipo(String tipo, Sort sort) {
        return tipoSolicitacaoRepository.findByTipo(tipo, sort);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorVisualizar(Boolean visualizar) {
        return tipoSolicitacaoRepository.findByVisualizar(visualizar);
    }
    
    // Overloaded method with sorting
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorVisualizar(Boolean visualizar, Sort sort) {
        return tipoSolicitacaoRepository.findByVisualizar(visualizar, sort);
    }
    
    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return tipoSolicitacaoRepository.existsById(id);
    }
}