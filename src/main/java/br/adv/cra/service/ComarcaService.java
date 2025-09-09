package br.adv.cra.service;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import br.adv.cra.repository.ComarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public List<Comarca> listarTodas() {
        return comarcaRepository.findAllOrderByNome();
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
        return comarcaRepository.findByNomeContaining(nome);
    }
    
    /**
     * Finds court districts by state (UF).
     * 
     * @param uf The state to search for
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUf(Uf uf) {
        return comarcaRepository.findByUf(uf);
    }
    
    /**
     * Finds court districts by state abbreviation (sigla).
     * 
     * @param sigla The state abbreviation to search for
     * @return List of court districts in the specified state
     */
    public List<Comarca> buscarPorUfSigla(String sigla) {
        return comarcaRepository.findByUfSiglaOrderByNome(sigla);
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