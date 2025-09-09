package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    
    Optional<Processo> findByNumeroprocesso(String numeroprocesso);
    
    @Query("SELECT p FROM Processo p WHERE p.numeroprocessopesq LIKE %:numero%")
    List<Processo> findByNumeroprocessopesqContaining(@Param("numero") String numero);
    
    @Query("SELECT p FROM Processo p WHERE p.parte LIKE %:parte%")
    List<Processo> findByParteContaining(@Param("parte") String parte);
    
    @Query("SELECT p FROM Processo p WHERE p.adverso LIKE %:adverso%")
    List<Processo> findByAdversoContaining(@Param("adverso") String adverso);
    
    List<Processo> findByStatus(String status);
    
    List<Processo> findByComarca(Comarca comarca);
    
    List<Processo> findByOrgao(Orgao orgao);
    
    @Query("SELECT p FROM Processo p WHERE p.assunto LIKE %:assunto%")
    List<Processo> findByAssuntoContaining(@Param("assunto") String assunto);
    
    List<Processo> findByProceletronico(String processoEletronico);
    
    boolean existsByNumeroprocesso(String numeroprocesso);
    
    @Query("SELECT COUNT(p) FROM Processo p WHERE p.status = :status")
    Long countByStatus(@Param("status") String status);
}