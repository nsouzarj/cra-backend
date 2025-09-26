package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    
    Optional<Processo> findByNumeroprocesso(String numeroprocesso);
    
    @Query("SELECT p FROM Processo p WHERE p.numeroprocessopesq LIKE CONCAT('%', :numero, '%')")
    List<Processo> findByNumeroprocessopesqContaining(@Param("numero") String numero);
    
    @Query("SELECT p FROM Processo p WHERE p.numeroprocessopesq LIKE CONCAT('%', :numero, '%')")
    List<Processo> findByNumeroprocessopesqContaining(@Param("numero") String numero, Sort sort);
    
    @Query("SELECT p FROM Processo p WHERE p.numeroprocessopesq LIKE CONCAT('%', :numero, '%')")
    Page<Processo> findByNumeroprocessopesqContaining(@Param("numero") String numero, Pageable pageable);
    
    @Query("SELECT p FROM Processo p WHERE p.parte LIKE CONCAT('%', :parte, '%')")
    List<Processo> findByParteContaining(@Param("parte") String parte);
    
    @Query("SELECT p FROM Processo p WHERE p.parte LIKE CONCAT('%', :parte, '%')")
    List<Processo> findByParteContaining(@Param("parte") String parte, Sort sort);
    
    @Query("SELECT p FROM Processo p WHERE p.parte LIKE CONCAT('%', :parte, '%')")
    Page<Processo> findByParteContaining(@Param("parte") String parte, Pageable pageable);
    
    @Query("SELECT p FROM Processo p WHERE p.adverso LIKE CONCAT('%', :adverso, '%')")
    List<Processo> findByAdversoContaining(@Param("adverso") String adverso);
    
    @Query("SELECT p FROM Processo p WHERE p.adverso LIKE CONCAT('%', :adverso, '%')")
    List<Processo> findByAdversoContaining(@Param("adverso") String adverso, Sort sort);
    
    @Query("SELECT p FROM Processo p WHERE p.adverso LIKE CONCAT('%', :adverso, '%')")
    Page<Processo> findByAdversoContaining(@Param("adverso") String adverso, Pageable pageable);
    
    List<Processo> findByStatus(String status);
    
    List<Processo> findByStatus(String status, Sort sort);
    
    Page<Processo> findByStatus(String status, Pageable pageable);
    
    List<Processo> findByComarca(Comarca comarca);
    
    List<Processo> findByComarca(Comarca comarca, Sort sort);
    
    Page<Processo> findByComarca(Comarca comarca, Pageable pageable);
    
    List<Processo> findByOrgao(Orgao orgao);
    
    List<Processo> findByOrgao(Orgao orgao, Sort sort);
    
    Page<Processo> findByOrgao(Orgao orgao, Pageable pageable);
    
    @Query("SELECT p FROM Processo p WHERE p.assunto LIKE CONCAT('%', :assunto, '%')")
    List<Processo> findByAssuntoContaining(@Param("assunto") String assunto);
    
    @Query("SELECT p FROM Processo p WHERE p.assunto LIKE CONCAT('%', :assunto, '%')")
    List<Processo> findByAssuntoContaining(@Param("assunto") String assunto, Sort sort);
    
    @Query("SELECT p FROM Processo p WHERE p.assunto LIKE CONCAT('%', :assunto, '%')")
    Page<Processo> findByAssuntoContaining(@Param("assunto") String assunto, Pageable pageable);
    
    List<Processo> findByProceletronico(String processoEletronico);
    
    List<Processo> findByProceletronico(String processoEletronico, Sort sort);
    
    Page<Processo> findByProceletronico(String processoEletronico, Pageable pageable);
    
    boolean existsByNumeroprocesso(String numeroprocesso);
    
    @Query("SELECT COUNT(p) FROM Processo p WHERE p.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Gets the total count of all processos.
     * 
     * @return Total count of processos
     */
    @Query("SELECT COUNT(p) FROM Processo p")
    long countAllProcessos();
}