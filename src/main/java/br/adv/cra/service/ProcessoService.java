package br.adv.cra.service;

import br.adv.cra.dto.ProcessoDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import br.adv.cra.repository.ProcessoRepository;
import br.adv.cra.repository.OrgaoRepository;
import br.adv.cra.repository.ComarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessoService {
    
    private final ProcessoRepository processoRepository;
    private final OrgaoRepository orgaoRepository;
    private final ComarcaRepository comarcaRepository;
    
    /**
     * Obtém um órgão existente pelo ID
     * @param orgaoId ID do órgão
     * @return Optional com o órgão encontrado
     */
    private Optional<Orgao> getOrgaoExistente(Long orgaoId) {
        if (orgaoId == null) {
            return Optional.empty();
        }
        return orgaoRepository.findById(orgaoId);
    }
    
    /**
     * Obtém uma comarca existente pelo ID
     * @param comarcaId ID da comarca
     * @return Optional com a comarca encontrada
     */
    private Optional<Comarca> getComarcaExistente(Long comarcaId) {
        if (comarcaId == null) {
            return Optional.empty();
        }
        return comarcaRepository.findById(comarcaId);
    }
    
    /**
     * Obtém um órgão existente pela descrição
     * @param descricao Descrição do órgão
     * @return Optional com o órgão encontrado
     */
    private Optional<Orgao> getOrgaoExistentePorDescricao(String descricao) {
        if (descricao == null || descricao.isEmpty()) {
            return Optional.empty();
        }
        List<Orgao> orgaos = orgaoRepository.findByDescricaoContaining(descricao);
        return orgaos.isEmpty() ? Optional.empty() : Optional.of(orgaos.get(0));
    }
    
    public Processo salvar(Processo processo) {
        // Extrai o ID do órgão se foi enviado um objeto Orgao completo
        Long orgaoId = null;
        if (processo.getOrgao() != null) {
            orgaoId = processo.getOrgao().getId();
        }
        
        // Extrai o ID da comarca se foi enviado um objeto Comarca completo
        Long comarcaId = null;
        if (processo.getComarca() != null) {
            comarcaId = processo.getComarca().getId();
        }
        
        // Desassocia completamente o órgão e comarca do processo para evitar criação acidental
        processo.setOrgao(null);
        processo.setComarca(null);
        
        // Associa o órgão existente, se fornecido
        if (orgaoId != null) {
            // Verifica se o órgão existe
            Optional<Orgao> orgaoExistente = getOrgaoExistente(orgaoId);
            if (orgaoExistente.isPresent()) {
                // Associa o órgão existente ao processo
                processo.setOrgao(orgaoExistente.get());
            } else {
                throw new RuntimeException("Órgão com ID " + orgaoId + " não encontrado");
            }
        }
        
        // Associa a comarca existente, se fornecida
        if (comarcaId != null) {
            // Verifica se a comarca existe
            Optional<Comarca> comarcaExistente = getComarcaExistente(comarcaId);
            if (comarcaExistente.isPresent()) {
                // Associa a comarca existente ao processo
                processo.setComarca(comarcaExistente.get());
            } else {
                throw new RuntimeException("Comarca com ID " + comarcaId + " não encontrada");
            }
        }
        
        // Salva o processo com as associações corretas
        return processoRepository.save(processo);
    }
    
    public Processo salvarComDTO(br.adv.cra.dto.ProcessoDTO processoDTO) {
        Processo processo = new Processo();
        processo.setId(processoDTO.getId());
        processo.setNumeroprocesso(processoDTO.getNumeroprocesso());
        processo.setNumeroprocessopesq(processoDTO.getNumeroprocessopesq());
        processo.setParte(processoDTO.getParte());
        processo.setAdverso(processoDTO.getAdverso());
        processo.setPosicao(processoDTO.getPosicao());
        processo.setStatus(processoDTO.getStatus());
        processo.setCartorio(processoDTO.getCartorio());
        processo.setAssunto(processoDTO.getAssunto());
        processo.setLocalizacao(processoDTO.getLocalizacao());
        processo.setNumerointegracao(processoDTO.getNumerointegracao());
        processo.setNumorgao(processoDTO.getNumorgao());
        processo.setProceletronico(processoDTO.getProceletronico());
        processo.setQuantsoli(processoDTO.getQuantsoli());
        processo.setDatadistribuicao(processoDTO.getDatadistribuicao());
        processo.setObservacao(processoDTO.getObservacao());
        
        // Associa o órgão existente, se fornecido
        if (processoDTO.getOrgaoId() != null) {
            Optional<Orgao> orgaoExistente = getOrgaoExistente(processoDTO.getOrgaoId());
            if (orgaoExistente.isPresent()) {
                processo.setOrgao(orgaoExistente.get());
            } else {
                throw new RuntimeException("Órgão com ID " + processoDTO.getOrgaoId() + " não encontrado");
            }
        }
        
        // Associa a comarca existente, se fornecida
        if (processoDTO.getComarcaId() != null) {
            Optional<Comarca> comarcaExistente = getComarcaExistente(processoDTO.getComarcaId());
            if (comarcaExistente.isPresent()) {
                processo.setComarca(comarcaExistente.get());
            } else {
                throw new RuntimeException("Comarca com ID " + processoDTO.getComarcaId() + " não encontrada");
            }
        }
        
        // Salva o processo com as associações corretas
        return processoRepository.save(processo);
    }
    
    public Processo atualizar(Processo processo) {
        if (!processoRepository.existsById(processo.getId())) {
            throw new RuntimeException("Processo não encontrado");
        }
        
        // Extrai o ID do órgão se foi enviado um objeto Orgao completo
        Long orgaoId = null;
        if (processo.getOrgao() != null) {
            orgaoId = processo.getOrgao().getId();
        }
        
        // Extrai o ID da comarca se foi enviado um objeto Comarca completo
        Long comarcaId = null;
        if (processo.getComarca() != null) {
            comarcaId = processo.getComarca().getId();
        }
        
        // Desassocia completamente o órgão e comarca do processo para evitar criação acidental
        processo.setOrgao(null);
        processo.setComarca(null);
        
        // Associa o órgão existente, se fornecido
        if (orgaoId != null) {
            // Verifica se o órgão existe
            Optional<Orgao> orgaoExistente = getOrgaoExistente(orgaoId);
            if (orgaoExistente.isPresent()) {
                // Associa o órgão existente ao processo
                processo.setOrgao(orgaoExistente.get());
            } else {
                throw new RuntimeException("Órgão com ID " + orgaoId + " não encontrado");
            }
        }
        
        // Associa a comarca existente, se fornecida
        if (comarcaId != null) {
            // Verifica se a comarca existe
            Optional<Comarca> comarcaExistente = getComarcaExistente(comarcaId);
            if (comarcaExistente.isPresent()) {
                // Associa a comarca existente ao processo
                processo.setComarca(comarcaExistente.get());
            } else {
                throw new RuntimeException("Comarca com ID " + comarcaId + " não encontrada");
            }
        }
        
        // Atualiza o processo com as associações
        return processoRepository.save(processo);
    }
    
    public void deletar(Long id) {
        if (!processoRepository.existsById(id)) {
            throw new RuntimeException("Processo não encontrado");
        }
        processoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Processo> buscarPorId(Long id) {
        return processoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ProcessoDTO> listarTodosDTO() {
        return processoRepository.findAll(Sort.by(Sort.Direction.ASC, "numeroprocesso")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private ProcessoDTO toDTO(Processo processo) {
        ProcessoDTO dto = new ProcessoDTO();
        dto.setId(processo.getId());
        dto.setNumeroprocesso(processo.getNumeroprocesso());
        dto.setNumeroprocessopesq(processo.getNumeroprocessopesq());
        dto.setParte(processo.getParte());
        dto.setAdverso(processo.getAdverso());
        dto.setPosicao(processo.getPosicao());
        dto.setStatus(processo.getStatus());
        dto.setCartorio(processo.getCartorio());
        dto.setAssunto(processo.getAssunto());
        dto.setLocalizacao(processo.getLocalizacao());
        dto.setNumerointegracao(processo.getNumerointegracao());
        dto.setNumorgao(processo.getNumorgao());
        dto.setProceletronico(processo.getProceletronico());
        dto.setQuantsoli(processo.getQuantsoli());
        dto.setDatadistribuicao(processo.getDatadistribuicao());
        dto.setObservacao(processo.getObservacao());
        
        if (processo.getComarca() != null) {
            dto.setComarcaId(processo.getComarca().getId());
            dto.setComarcaNome(processo.getComarca().getNome());
        }
        
        if (processo.getOrgao() != null) {
            dto.setOrgaoId(processo.getOrgao().getId());
            dto.setOrgaoDescricao(processo.getOrgao().getDescricao());
        }
        
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<Processo> listarTodos() {
        return processoRepository.findAll(Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> listarTodos(Sort sort) {
        return processoRepository.findAll(sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> listarTodos(Pageable pageable) {
        return processoRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ProcessoDTO> listarTodosDTO(Pageable pageable) {
        Page<Processo> processos = processoRepository.findAll(pageable);
        return processos.map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public long contarTodos() {
        return processoRepository.countAllProcessos();
    }
    
    @Transactional(readOnly = true)
    public Optional<Processo> buscarPorNumeroProcesso(String numeroprocesso) {
        return processoRepository.findByNumeroprocesso(numeroprocesso);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorNumeroProcessoPesquisa(String numero) {
        return processoRepository.findByNumeroprocessopesqContaining(numero, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorNumeroProcessoPesquisa(String numero, Sort sort) {
        return processoRepository.findByNumeroprocessopesqContaining(numero, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorNumeroProcessoPesquisa(String numero, Pageable pageable) {
        return processoRepository.findByNumeroprocessopesqContaining(numero, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorParte(String parte) {
        return processoRepository.findByParteContaining(parte, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorParte(String parte, Sort sort) {
        return processoRepository.findByParteContaining(parte, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorParte(String parte, Pageable pageable) {
        return processoRepository.findByParteContaining(parte, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorAdverso(String adverso) {
        return processoRepository.findByAdversoContaining(adverso, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorAdverso(String adverso, Sort sort) {
        return processoRepository.findByAdversoContaining(adverso, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorAdverso(String adverso, Pageable pageable) {
        return processoRepository.findByAdversoContaining(adverso, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorStatus(String status) {
        return processoRepository.findByStatus(status, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorStatus(String status, Sort sort) {
        return processoRepository.findByStatus(status, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorStatus(String status, Pageable pageable) {
        return processoRepository.findByStatus(status, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorComarca(Comarca comarca) {
        return processoRepository.findByComarca(comarca, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorComarca(Comarca comarca, Sort sort) {
        return processoRepository.findByComarca(comarca, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorComarca(Comarca comarca, Pageable pageable) {
        return processoRepository.findByComarca(comarca, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorOrgao(Orgao orgao) {
        return processoRepository.findByOrgao(orgao, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorOrgao(Orgao orgao, Sort sort) {
        return processoRepository.findByOrgao(orgao, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorOrgao(Orgao orgao, Pageable pageable) {
        return processoRepository.findByOrgao(orgao, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorAssunto(String assunto) {
        return processoRepository.findByAssuntoContaining(assunto, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorAssunto(String assunto, Sort sort) {
        return processoRepository.findByAssuntoContaining(assunto, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorAssunto(String assunto, Pageable pageable) {
        return processoRepository.findByAssuntoContaining(assunto, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorProcessoEletronico(String processoEletronico) {
        return processoRepository.findByProceletronico(processoEletronico, Sort.by(Sort.Direction.ASC, "numeroprocesso"));
    }
    
    @Transactional(readOnly = true)
    public List<Processo> buscarPorProcessoEletronico(String processoEletronico, Sort sort) {
        return processoRepository.findByProceletronico(processoEletronico, sort);
    }
    
    @Transactional(readOnly = true)
    public Page<Processo> buscarPorProcessoEletronico(String processoEletronico, Pageable pageable) {
        return processoRepository.findByProceletronico(processoEletronico, pageable);
    }
    
    @Transactional(readOnly = true)
    public boolean existeNumeroProcesso(String numeroprocesso) {
        return processoRepository.existsByNumeroprocesso(numeroprocesso);
    }
    
    @Transactional(readOnly = true)
    public boolean existeNumeroProcessoParaOutroProcesso(String numeroprocesso, Long id) {
        Optional<Processo> processo = processoRepository.findByNumeroprocesso(numeroprocesso);
        return processo.isPresent() && !processo.get().getId().equals(id);
    }
    
    @Transactional(readOnly = true)
    public Long contarPorStatus(String status) {
        return processoRepository.countByStatus(status);
    }
}