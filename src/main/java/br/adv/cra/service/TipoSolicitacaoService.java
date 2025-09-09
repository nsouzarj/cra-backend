package br.adv.cra.service;

import br.adv.cra.entity.TipoSolicitacao;
import br.adv.cra.repository.TipoSolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public List<TipoSolicitacao> listarTodos() {
        return tipoSolicitacaoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> listarTodosOrdenados() {
        return tipoSolicitacaoRepository.findAllOrderByEspecie();
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorEspecie(String especie) {
        return tipoSolicitacaoRepository.findByEspecieContaining(especie);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorDescricao(String descricao) {
        return tipoSolicitacaoRepository.findByDescricaoContaining(descricao);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorTipo(String tipo) {
        return tipoSolicitacaoRepository.findByTipo(tipo);
    }
    
    @Transactional(readOnly = true)
    public List<TipoSolicitacao> buscarPorVisualizar(Boolean visualizar) {
        return tipoSolicitacaoRepository.findByVisualizar(visualizar);
    }
    
    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return tipoSolicitacaoRepository.existsById(id);
    }
}