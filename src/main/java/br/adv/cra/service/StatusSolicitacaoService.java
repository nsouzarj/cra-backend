package br.adv.cra.service;

import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.repository.StatusSolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusSolicitacaoService {
    
    private static final Logger log = LoggerFactory.getLogger(StatusSolicitacaoService.class);
    
    private final StatusSolicitacaoRepository statusSolicitacaoRepository;
    
    public StatusSolicitacao salvar(StatusSolicitacao statusSolicitacao) {
        return statusSolicitacaoRepository.save(statusSolicitacao);
    }
    
    public StatusSolicitacao atualizar(StatusSolicitacao statusSolicitacao) {
        if (!statusSolicitacaoRepository.existsById(statusSolicitacao.getIdstatus())) {
            throw new RuntimeException("Status de solicitação não encontrado");
        }
        return statusSolicitacaoRepository.save(statusSolicitacao);
    }
    
    public void deletar(Long id) {
        if (!statusSolicitacaoRepository.existsById(id)) {
            throw new RuntimeException("Status de solicitação não encontrado");
        }
        statusSolicitacaoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<StatusSolicitacao> buscarPorId(Long id) {
        log.info("Buscando status de solicitação por ID: {}", id);
        return statusSolicitacaoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<StatusSolicitacao> buscarPorStatus(String status) {
        log.info("Buscando status de solicitação por status exato: {}", status);
        return statusSolicitacaoRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<StatusSolicitacao> listarTodos() {
        try {
            log.info("Listando todos os status de solicitação");
            List<StatusSolicitacao> status = statusSolicitacaoRepository.findAllOrderByStatus();
            log.info("Total de status de solicitação encontrados: {}", status.size());
            return status;
        } catch (Exception e) {
            log.error("Erro ao listar status de solicitação", e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<StatusSolicitacao> buscarPorStatusContaining(String status) {
        try {
            log.info("Buscando status de solicitação por status contendo: {}", status);
            List<StatusSolicitacao> statusList = statusSolicitacaoRepository.findByStatusContaining(status);
            log.info("Total de status de solicitação encontrados com status contendo '{}': {}", status, statusList.size());
            return statusList;
        } catch (Exception e) {
            log.error("Erro ao buscar status de solicitação por status contendo: {}", status, e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean existeStatus(String status) {
        try {
            log.info("Verificando existência de status de solicitação com status: {}", status);
            List<StatusSolicitacao> statusList = statusSolicitacaoRepository.findByStatusContaining(status);
            boolean existe = statusList.size() > 0;
            log.info("Existe status de solicitação com status '{}': {}", status, existe);
            return existe;
        } catch (Exception e) {
            log.error("Erro ao verificar existência de status de solicitação com status: {}", status, e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean existeStatusParaOutroStatus(String status, Long id) {
        try {
            log.info("Verificando existência de status de solicitação com status '{}' para outro ID: {}", status, id);
            List<StatusSolicitacao> statusList = statusSolicitacaoRepository.findByStatusContaining(status);
            boolean existe = statusList.stream().anyMatch(s -> !s.getIdstatus().equals(id));
            log.info("Existe status de solicitação com status '{}' para outro ID: {}", status, existe);
            return existe;
        } catch (Exception e) {
            log.error("Erro ao verificar existência de status de solicitação com status '{}' para outro ID: {}", status, id, e);
            throw e;
        }
    }
}