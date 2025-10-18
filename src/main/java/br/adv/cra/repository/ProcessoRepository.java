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

    List<Processo> findByNumeroprocessopesqContaining(String numero, Sort sort);

    Page<Processo> findByNumeroprocessopesqContaining(String numero, Pageable pageable);

    List<Processo> findByParteContaining(String parte, Sort sort);

    Page<Processo> findByParteContaining(String parte, Pageable pageable);

    List<Processo> findByAdversoContaining(String adverso, Sort sort);

    Page<Processo> findByAdversoContaining(String adverso, Pageable pageable);

    List<Processo> findByStatus(String status, Sort sort);

    Page<Processo> findByStatus(String status, Pageable pageable);

    List<Processo> findByComarca(Comarca comarca, Sort sort);

    Page<Processo> findByComarca(Comarca comarca, Pageable pageable);

    List<Processo> findByOrgao(Orgao orgao, Sort sort);

    Page<Processo> findByOrgao(Orgao orgao, Pageable pageable);

    List<Processo> findByAssuntoContaining(String assunto, Sort sort);

    Page<Processo> findByAssuntoContaining(String assunto, Pageable pageable);

    List<Processo> findByProceletronico(String processoEletronico, Sort sort);

    Page<Processo> findByProceletronico(String processoEletronico, Pageable pageable);

    boolean existsByNumeroprocesso(String numeroprocesso);

    @Query("SELECT (COUNT(p) > 0) FROM Processo p WHERE p.numeroprocesso = :numeroprocesso AND p.id <> :id")
    boolean existsByNumeroprocessoAndIdNot(@Param("numeroprocesso") String numeroprocesso, @Param("id") Long id);

    Long countByStatus(String status);

    @Query("SELECT COUNT(p) FROM Processo p")
    long countAllProcessos();
}