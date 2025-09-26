package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    
    List<Solicitacao> findByUsuario(Usuario usuario);
    
    List<Solicitacao> findByUsuario(Usuario usuario, Sort sort);
    
    Page<Solicitacao> findByUsuario(Usuario usuario, Pageable pageable);
    
    List<Solicitacao> findByProcesso(Processo processo);
    
    List<Solicitacao> findByProcesso(Processo processo, Sort sort);
    
    Page<Solicitacao> findByProcesso(Processo processo, Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.comarca = :comarca OR s.processo.comarca = :comarca")
    List<Solicitacao> findByComarca(@Param("comarca") Comarca comarca);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.comarca = :comarca OR s.processo.comarca = :comarca")
    List<Solicitacao> findByComarca(@Param("comarca") Comarca comarca, Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.comarca = :comarca OR s.processo.comarca = :comarca")
    Page<Solicitacao> findByComarca(@Param("comarca") Comarca comarca, Pageable pageable);
    
    // Adding method to find solicitacoes by correspondente
    List<Solicitacao> findByCorrespondente(Correspondente correspondente);
    
    List<Solicitacao> findByCorrespondente(Correspondente correspondente, Sort sort);
    
    Page<Solicitacao> findByCorrespondente(Correspondente correspondente, Pageable pageable);
    
    // Adding method to find solicitacoes by usuario and correspondente
    List<Solicitacao> findByUsuarioAndCorrespondente(Usuario usuario, Correspondente correspondente);
    
    List<Solicitacao> findByUsuarioAndCorrespondente(Usuario usuario, Correspondente correspondente, Sort sort);
    
    Page<Solicitacao> findByUsuarioAndCorrespondente(Usuario usuario, Correspondente correspondente, Pageable pageable);
    
    // Adding method to find solicitacoes by comarca and correspondente
    // This query finds solicitacoes where the comarca matches directly OR through the processo, and the correspondente matches
    @Query("SELECT s FROM Solicitacao s WHERE (s.comarca = :comarca OR s.processo.comarca = :comarca) AND s.correspondente = :correspondente")
    Page<Solicitacao> findByComarcaAndCorrespondente(@Param("comarca") Comarca comarca, @Param("correspondente") Correspondente correspondente, Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.datasolicitacao BETWEEN :inicio AND :fim")
    List<Solicitacao> findByDatasolicitacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.datasolicitacao BETWEEN :inicio AND :fim")
    List<Solicitacao> findByDatasolicitacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.datasolicitacao BETWEEN :inicio AND :fim")
    Page<Solicitacao> findByDatasolicitacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    List<Solicitacao> findPendentes();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    List<Solicitacao> findPendentes(Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    Page<Solicitacao> findPendentes(Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NOT NULL")
    List<Solicitacao> findConcluidas();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NOT NULL")
    List<Solicitacao> findConcluidas(Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NOT NULL")
    Page<Solicitacao> findConcluidas(Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'true'")
    List<Solicitacao> findByPagoTrue();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'true'")
    List<Solicitacao> findByPagoTrue(Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'true'")
    Page<Solicitacao> findByPagoTrue(Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'false'")
    List<Solicitacao> findByPagoFalse();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'false'")
    List<Solicitacao> findByPagoFalse(Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'false'")
    Page<Solicitacao> findByPagoFalse(Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataagendamento < :data AND s.dataconclusao IS NULL")
    List<Solicitacao> findAtrasadas(@Param("data") LocalDateTime data);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataagendamento < :data AND s.dataconclusao IS NULL")
    List<Solicitacao> findAtrasadas(@Param("data") LocalDateTime data, Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataagendamento < :data AND s.dataconclusao IS NULL")
    Page<Solicitacao> findAtrasadas(@Param("data") LocalDateTime data, Pageable pageable);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.observacao LIKE '%' || :texto || '%' OR s.instrucoes LIKE '%' || :texto || '%'")
    List<Solicitacao> findByTextoContaining(@Param("texto") String texto);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.observacao LIKE '%' || :texto || '%' OR s.instrucoes LIKE '%' || :texto || '%'")
    List<Solicitacao> findByTextoContaining(@Param("texto") String texto, Sort sort);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.observacao LIKE '%' || :texto || '%' OR s.instrucoes LIKE '%' || :texto || '%'")
    Page<Solicitacao> findByTextoContaining(@Param("texto") String texto, Pageable pageable);
    
    List<Solicitacao> findByGrupo(Integer grupo);
    
    List<Solicitacao> findByGrupo(Integer grupo, Sort sort);
    
    Page<Solicitacao> findByGrupo(Integer grupo, Pageable pageable);
    
    List<Solicitacao> findByStatusexterno(String statusexterno);
    
    List<Solicitacao> findByStatusexterno(String statusexterno, Sort sort);
    
    Page<Solicitacao> findByStatusexterno(String statusexterno, Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.usuario = :usuario")
    Long countByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    Long countPendentes();
    
    // Method to delete historico records by solicitacao id
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM historico WHERE idsolicitacao = :solicitacaoId", nativeQuery = true)
    void deleteHistoricoBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);
    
    /**
     * Gets the total count of all solicitacoes.
     * 
     * @return Total count of solicitacoes
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s")
    long countAllSolicitacoes();
    
    /**
     * Gets count of solicitacoes grouped by comarca
     * 
     * @return List of comarca counts
     */
    @Query("SELECT s.comarca.id, s.comarca.nome, COUNT(s) FROM Solicitacao s WHERE s.comarca IS NOT NULL GROUP BY s.comarca.id, s.comarca.nome ORDER BY COUNT(s) DESC")
    List<Object[]> countByComarca();
    
    /**
     * Gets count of solicitacoes grouped by tipo solicitacao
     * 
     * @return List of tipo solicitacao counts
     */
    @Query("SELECT s.tipoSolicitacao.idtiposolicitacao, s.tipoSolicitacao.especie, COUNT(s) FROM Solicitacao s WHERE s.tipoSolicitacao IS NOT NULL GROUP BY s.tipoSolicitacao.idtiposolicitacao, s.tipoSolicitacao.especie ORDER BY COUNT(s) DESC")
    List<Object[]> countByTipoSolicitacao();
    
    /**
     * Gets count of solicitacoes grouped by status
     * 
     * @return List of status counts
     */
    @Query("SELECT s.statusSolicitacao.status, COUNT(s) FROM Solicitacao s WHERE s.statusSolicitacao IS NOT NULL GROUP BY s.statusSolicitacao.status ORDER BY COUNT(s) DESC")
    List<Object[]> countByStatusSolicitacao();
    
    /**
     * Gets count of solicitacoes for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return Total count of solicitacoes for the correspondent
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente")
    Long countByCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of pending solicitacoes for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return Count of pending solicitacoes for the correspondent
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.dataconclusao IS NULL")
    Long countPendentesByCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of paid solicitacoes for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return Count of paid solicitacoes for the correspondent
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.pago = 'true'")
    Long countPagasByCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of unpaid solicitacoes for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return Count of unpaid solicitacoes for the correspondent
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.pago = 'false'")
    Long countNaoPagasByCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of overdue solicitacoes for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @param data The current date for comparison
     * @return Count of overdue solicitacoes for the correspondent
     */
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.dataagendamento < :data AND s.dataconclusao IS NULL")
    Long countAtrasadasByCorrespondente(@Param("correspondente") Correspondente correspondente, @Param("data") LocalDateTime data);
    
    /**
     * Gets count of solicitacoes by comarca for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return List of comarca counts for the correspondent
     */
    @Query("SELECT s.comarca.id, s.comarca.nome, COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.comarca IS NOT NULL GROUP BY s.comarca.id, s.comarca.nome ORDER BY COUNT(s) DESC")
    List<Object[]> countByComarcaAndCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of solicitacoes by tipo solicitacao for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return List of tipo solicitacao counts for the correspondent
     */
    @Query("SELECT s.tipoSolicitacao.idtiposolicitacao, s.tipoSolicitacao.especie, COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.tipoSolicitacao IS NOT NULL GROUP BY s.tipoSolicitacao.idtiposolicitacao, s.tipoSolicitacao.especie ORDER BY COUNT(s) DESC")
    List<Object[]> countByTipoSolicitacaoAndCorrespondente(@Param("correspondente") Correspondente correspondente);
    
    /**
     * Gets count of solicitacoes by status for a specific correspondent
     * 
     * @param correspondente The correspondent to filter by
     * @return List of status counts for the correspondent
     */
    @Query("SELECT s.statusSolicitacao.status, COUNT(s) FROM Solicitacao s WHERE s.correspondente = :correspondente AND s.statusSolicitacao IS NOT NULL GROUP BY s.statusSolicitacao.status ORDER BY COUNT(s) DESC")
    List<Object[]> countByStatusSolicitacaoAndCorrespondente(@Param("correspondente") Correspondente correspondente);
}