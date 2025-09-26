package br.adv.cra.service;

import br.adv.cra.dto.ComarcaDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.ComarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing court districts (Comarcas).
 * 
 * This service provides business logic for court district operations,
 * including CRUD operations and specialized search capabilities.
 */
@Service
@RequiredArgsConstructor
public class ComarcaService {
    
    private final ComarcaRepository comarcaRepository;
    
    /**
     * Saves a court district.
     * 
     * @param comarca The court district to save
     * @return The saved court district
     */
    public Comarca salvar(Comarca comarca) {
        return comarcaRepository.save(comarca);
    }
    
    /**
     * Updates a court district.
     * 
     * @param comarca The court district to update
     * @return The updated court district
     */
    public Comarca atualizar(Comarca comarca) {
        return comarcaRepository.save(comarca);
    }
    
    /**
     * Lists all court districts ordered by name.
     * 
     * @return List of all court districts ordered by name
     */
    @Cacheable(value = "comarcas", key = "'all_comarcas'")
    public List<Comarca> listarTodas() {
        return comarcaRepository.findAllOrderByNome(Sort.by(Sort.Direction.ASC, "nome"));
    }
    
    /**
     * Lists all court districts with pagination.
     * 
     * @param pageable Pagination information
     * @return Page of court districts
     */
    public Page<Comarca> listarTodas(Pageable pageable) {
        return comarcaRepository.findAll(pageable);
    }
    
    /**
     * Lists all court districts ordered by name with pagination.
     * 
     * @param pageable Pagination information
     * @return Page of court districts ordered by name
     */
    public Page<Comarca> listarTodasOrdenadas(Pageable pageable) {
        return comarcaRepository.findAllOrderByNome(pageable);
    }
    
    /**
     * Gets the total count of all court districts.
     * 
     * @return Total count of court districts
     */
    public long contarTodas() {
        return comarcaRepository.countAllComarcas();
    }
    
    /**
     * Lists all court districts DTO ordered by name.
     * 
     * @return List of all court districts DTO ordered by name
     */
    @Cacheable(value = "comarcas", key = "'all_comarcas_dto'")
    public List<ComarcaDTO> listarTodasDTO() {
        return comarcaRepository.findAllOrderByNome(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lists all court districts DTO with pagination.
     * 
     * @param pageable Pagination information
     * @return Page of court districts DTO
     */
    public Page<ComarcaDTO> listarTodasDTO(Pageable pageable) {
        Page<Comarca> comarcas = comarcaRepository.findAll(pageable);
        return comarcas.map(this::toDTO);
    }
    
    private ComarcaDTO toDTO(Comarca comarca) {
        ComarcaDTO dto = new ComarcaDTO();
        dto.setId(comarca.getId());
        dto.setNome(comarca.getNome());
        dto.setAtivo(comarca.isAtivo());
        
        if (comarca.getUf() != null) {
            dto.setUfId(comarca.getUf().getId());
            dto.setUfSigla(comarca.getUf().getSigla());
            dto.setUfNome(comarca.getUf().getNome());
        }
        
        return dto;
    }
    
    /**
     * Lists all court districts with custom sorting.
     * 
     * @param sort The sorting criteria
     * @return List of all court districts ordered by the specified criteria
     */
    @Cacheable(value = "comarcas", key = "#sort")
    public List<Comarca> listarTodas(Sort sort) {
        return comarcaRepository.findAll(sort);
    }
    
    /**
     * Retrieves a court district by ID.
     * 
     * @param id The ID of the court district
     * @return Optional containing the court district if found
     */
    public Optional<Comarca> buscarPorId(Long id) {
        return comarcaRepository.findById(id);
    }
    
    /**
     * Searches court districts by name (partial match).
     * 
     * @param nome The name to search for
     * @return List of matching court districts
     */
    public List<Comarca> buscarPorNome(String nome) {
        return comarcaRepository.findByNomeContaining(nome, Sort.by(Sort.Direction.ASC, "nome"));
    }
    
    /**
     * Searches court districts by name (partial match) with pagination.
     * 
     * @param nome The name to search for
     * @param pageable Pagination information
     * @return Page of matching court districts
     */
    public Page<Comarca> buscarPorNome(String nome, Pageable pageable) {
        return comarcaRepository.findByNomeContaining(nome, pageable);
    }
    
    /**
     * Gets the count of court districts matching a name search.
     * 
     * @param nome The name to search for
     * @return Count of matching court districts
     */
    public long contarPorNome(String nome) {
        return comarcaRepository.countByNomeContaining(nome);
    }
    
    /**
     * Searches court districts by name (partial match) with custom sorting.
     * 
     * @param nome The name to search for
     * @param sort The sorting criteria
     * @return List of matching court districts
     */
    public List<Comarca> buscarPorNome(String nome, Sort sort) {
        return comarcaRepository.findByNomeContaining(nome, sort);
    }
    
    /**
     * Finds court districts by state (UF).
     * 
     * @param uf The state to search for
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUf(Uf uf) {
        return comarcaRepository.findByUf(uf, Sort.by(Sort.Direction.ASC, "nome"));
    }
    
    /**
     * Finds court districts by state (UF) with pagination.
     * 
     * @param uf The state to search for
     * @param pageable Pagination information
     * @return Page of court districts in the specified state
     */
    public Page<Comarca> buscarPorUf(Uf uf, Pageable pageable) {
        return comarcaRepository.findByUf(uf, pageable);
    }
    
    /**
     * Gets the count of court districts in a state.
     * 
     * @param uf The state to search for
     * @return Count of court districts in the specified state
     */
    public long contarPorUf(Uf uf) {
        return comarcaRepository.countByUfId(uf.getId());
    }
    
    /**
     * Gets the count of court districts in a state by ID.
     * 
     * @param ufId The state ID to search for
     * @return Count of court districts in the specified state
     */
    public long contarPorUfId(Long ufId) {
        return comarcaRepository.countByUfId(ufId);
    }
    
    /**
     * Finds court districts by state (UF) with custom sorting.
     * 
     * @param uf The state to search for
     * @param sort The sorting criteria
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUf(Uf uf, Sort sort) {
        return comarcaRepository.findByUf(uf, sort);
    }
    
    /**
     * Finds court districts by state (UF) ID with pagination.
     * 
     * @param ufId The state ID to search for
     * @param pageable Pagination information
     * @return Page of court districts in the specified state
     */
    public Page<Comarca> buscarPorUfId(Long ufId, Pageable pageable) {
        return comarcaRepository.findByUfId(ufId, pageable);
    }
    
    /**
     * Finds court districts by state abbreviation (sigla).
     * 
     * @param sigla The state abbreviation to search for
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUfSigla(String sigla) {
        return comarcaRepository.findByUfSiglaOrderByNome(sigla, Sort.by(Sort.Direction.ASC, "nome"));
    }
    
    /**
     * Finds court districts by state abbreviation (sigla) with pagination.
     * 
     * @param sigla The state abbreviation to search for
     * @param pageable Pagination information
     * @return Page of court districts in the specified state
     */
    public Page<Comarca> buscarPorUfSigla(String sigla, Pageable pageable) {
        return comarcaRepository.findByUfSiglaOrderByNome(sigla, pageable);
    }
    
    /**
     * Gets the count of court districts in a state by sigla.
     * 
     * @param sigla The state abbreviation to search for
     * @return Count of court districts in the specified state
     */
    public long contarPorUfSigla(String sigla) {
        return comarcaRepository.countByUfSigla(sigla);
    }
    
    /**
     * Finds court districts by state abbreviation (sigla) with custom sorting.
     * 
     * @param sigla The state abbreviation to search for
     * @param sort The sorting criteria
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUfSigla(String sigla, Sort sort) {
        return comarcaRepository.findByUfSiglaOrderByNome(sigla, sort);
    }
    
    /**
     * Deletes a court district by ID.
     * 
     * @param id The ID of the court district to delete
     */
    public void deletar(Long id) {
        comarcaRepository.deleteById(id);
    }
}