package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComarcaRepository extends JpaRepository<Comarca, Long> {
    
    List<Comarca> findByUf(Uf uf);
    
    List<Comarca> findByUf(Uf uf, Sort sort);
    
    Page<Comarca> findByUf(Uf uf, Pageable pageable);
    
    @Query("SELECT c FROM Comarca c WHERE c.nome LIKE CONCAT('%', :nome, '%')")
    List<Comarca> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT c FROM Comarca c WHERE c.nome LIKE CONCAT('%', :nome, '%')")
    List<Comarca> findByNomeContaining(@Param("nome") String nome, Sort sort);
    
    @Query("SELECT c FROM Comarca c WHERE c.nome LIKE CONCAT('%', :nome, '%')")
    Page<Comarca> findByNomeContaining(@Param("nome") String nome, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comarca c WHERE c.nome LIKE CONCAT('%', :nome, '%')")
    long countByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT c FROM Comarca c ORDER BY c.nome")
    List<Comarca> findAllOrderByNome();
    
    @Query("SELECT c FROM Comarca c ORDER BY c.nome")
    List<Comarca> findAllOrderByNome(Sort sort);
    
    @Query("SELECT c FROM Comarca c ORDER BY c.nome")
    Page<Comarca> findAllOrderByNome(Pageable pageable);
    
    @Query("SELECT c FROM Comarca c WHERE c.uf.sigla = :sigla ORDER BY c.nome")
    List<Comarca> findByUfSiglaOrderByNome(@Param("sigla") String sigla);
    
    @Query("SELECT c FROM Comarca c WHERE c.uf.sigla = :sigla ORDER BY c.nome")
    List<Comarca> findByUfSiglaOrderByNome(@Param("sigla") String sigla, Sort sort);
    
    @Query("SELECT c FROM Comarca c WHERE c.uf.sigla = :sigla ORDER BY c.nome")
    Page<Comarca> findByUfSiglaOrderByNome(@Param("sigla") String sigla, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comarca c WHERE c.uf.sigla = :sigla")
    long countByUfSigla(@Param("sigla") String sigla);
    
    Page<Comarca> findByUfId(Long ufId, Pageable pageable);
    
    long countByUfId(Long ufId);
    
    /**
     * Gets the total count of all court districts.
     * 
     * @return Total count of court districts
     */
    @Query("SELECT COUNT(c) FROM Comarca c")
    long countAllComarcas();
    
    /**
     * Gets the count of active court districts.
     * 
     * @return Count of active court districts
     */
    @Query("SELECT COUNT(c) FROM Comarca c WHERE c.ativo = true")
    long countAtivas();
    
    /**
     * Gets the count of inactive court districts.
     * 
     * @return Count of inactive court districts
     */
    @Query("SELECT COUNT(c) FROM Comarca c WHERE c.ativo = false")
    long countInativas();
}