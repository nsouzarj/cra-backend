package br.adv.cra.service;

import br.adv.cra.entity.Orgao;
import br.adv.cra.repository.OrgaoRepository;
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
public class OrgaoService {
    
    private static final Logger log = LoggerFactory.getLogger(OrgaoService.class);
    
    private final OrgaoRepository orgaoRepository;
    
    public Orgao salvar(Orgao orgao) {
        return orgaoRepository.save(orgao);
    }
    
    public Orgao atualizar(Orgao orgao) {
        if (!orgaoRepository.existsById(orgao.getId())) {
            throw new RuntimeException("Órgão não encontrado");
        }
        return orgaoRepository.save(orgao);
    }
    
    public void deletar(Long id) {
        if (!orgaoRepository.existsById(id)) {
            throw new RuntimeException("Órgão não encontrado");
        }
        orgaoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Orgao> buscarPorId(Long id) {
        log.info("Buscando órgão por ID: {}", id);
        Optional<Orgao> orgao = orgaoRepository.findById(id);
        log.info("Órgão encontrado: {}", orgao.isPresent());
        return orgao;
    }
    
    @Transactional(readOnly = true)
    public List<Orgao> listarTodos() {
        try {
            log.info("Contando total de órgãos");
            long count = orgaoRepository.count();
            log.info("Total de órgãos no banco de dados: {}", count);
            
            log.info("Listando todos os órgãos (findAll)");
            List<Orgao> allOrgaos = orgaoRepository.findAll();
            log.info("Total de órgãos encontrados com findAll: {}", allOrgaos.size());
            allOrgaos.forEach(orgao -> log.info("Órgão (findAll): ID={}, Descrição={}", orgao.getId(), orgao.getDescricao()));
            
            log.info("Listando todos os órgãos ordenados por descrição");
            List<Orgao> orgaos = orgaoRepository.findAllOrderByDescricao();
            log.info("Total de órgãos encontrados: {}", orgaos.size());
            orgaos.forEach(orgao -> log.info("Órgão: ID={}, Descrição={}", orgao.getId(), orgao.getDescricao()));
            return orgaos;
        } catch (Exception e) {
            log.error("Erro ao listar órgãos", e);
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Orgao> buscarPorDescricao(String descricao) {
        try {
            log.info("Buscando órgãos por descrição: {}", descricao);
            List<Orgao> orgaos = orgaoRepository.findByDescricaoContaining(descricao);
            log.info("Total de órgãos encontrados com descrição '{}': {}", descricao, orgaos.size());
            return orgaos;
        } catch (Exception e) {
            log.error("Erro ao buscar órgãos por descrição: {}", descricao, e);
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean existeDescricao(String descricao) {
        try {
            log.info("Verificando existência de órgão com descrição: {}", descricao);
            List<Orgao> orgaos = orgaoRepository.findByDescricaoContaining(descricao);
            boolean existe = orgaos.size() > 0;
            log.info("Existe órgão com descrição '{}': {}", descricao, existe);
            return existe;
        } catch (Exception e) {
            log.error("Erro ao verificar existência de órgão com descrição: {}", descricao, e);
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean existeDescricaoParaOutroOrgao(String descricao, Long id) {
        try {
            log.info("Verificando existência de órgão com descrição '{}' para outro ID: {}", descricao, id);
            List<Orgao> orgaos = orgaoRepository.findByDescricaoContaining(descricao);
            boolean existe = orgaos.stream().anyMatch(o -> !o.getId().equals(id));
            log.info("Existe órgão com descrição '{}' para outro ID: {}", descricao, existe);
            return existe;
        } catch (Exception e) {
            log.error("Erro ao verificar existência de órgão com descrição '{}' para outro ID: {}", descricao, id, e);
            e.printStackTrace();
            throw e;
        }
    }
}