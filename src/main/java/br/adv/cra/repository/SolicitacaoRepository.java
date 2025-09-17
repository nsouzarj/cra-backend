package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.Usuario;
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
    
    List<Solicitacao> findByProcesso(Processo processo);
    
    List<Solicitacao> findByComarca(Comarca comarca);
    
    // Adding method to find solicitacoes by correspondente
    List<Solicitacao> findByCorrespondente(Correspondente correspondente);
    
    // Adding method to find solicitacoes by usuario and correspondente
    List<Solicitacao> findByUsuarioAndCorrespondente(Usuario usuario, Correspondente correspondente);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.datasolicitacao BETWEEN :inicio AND :fim")
    List<Solicitacao> findByDatasolicitacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    List<Solicitacao> findPendentes();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataconclusao IS NOT NULL")
    List<Solicitacao> findConcluidas();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'true'")
    List<Solicitacao> findByPagoTrue();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.pago = 'false'")
    List<Solicitacao> findByPagoFalse();
    
    @Query("SELECT s FROM Solicitacao s WHERE s.dataagendamento < :data AND s.dataconclusao IS NULL")
    List<Solicitacao> findAtrasadas(@Param("data") LocalDateTime data);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.observacao LIKE '%' || :texto || '%' OR s.instrucoes LIKE '%' || :texto || '%'")
    List<Solicitacao> findByTextoContaining(@Param("texto") String texto);
    
    List<Solicitacao> findByGrupo(Integer grupo);
    
    List<Solicitacao> findByStatusexterno(String statusexterno);
    
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.usuario = :usuario")
    Long countByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT COUNT(s) FROM Solicitacao s WHERE s.dataconclusao IS NULL")
    Long countPendentes();
    
    // Method to delete historico records by solicitacao id
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM historico WHERE idsolicitacao = :solicitacaoId", nativeQuery = true)
    void deleteHistoricoBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);
}