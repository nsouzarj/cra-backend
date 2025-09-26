package br.adv.cra.repository;

import br.adv.cra.entity.TipoSolicitacao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoSolicitacaoRepository extends JpaRepository<TipoSolicitacao, Long> {
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.especie LIKE CONCAT('%', :especie, '%')")
    List<TipoSolicitacao> findByEspecieContaining(@Param("especie") String especie);
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.especie LIKE CONCAT('%', :especie, '%')")
    List<TipoSolicitacao> findByEspecieContaining(@Param("especie") String especie, Sort sort);
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.descricao LIKE CONCAT('%', :descricao, '%')")
    List<TipoSolicitacao> findByDescricaoContaining(@Param("descricao") String descricao);
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.descricao LIKE CONCAT('%', :descricao, '%')")
    List<TipoSolicitacao> findByDescricaoContaining(@Param("descricao") String descricao, Sort sort);
    
    List<TipoSolicitacao> findByTipo(String tipo);
    
    List<TipoSolicitacao> findByTipo(String tipo, Sort sort);
    
    List<TipoSolicitacao> findByVisualizar(Boolean visualizar);
    
    List<TipoSolicitacao> findByVisualizar(Boolean visualizar, Sort sort);
    
    @Query("SELECT t FROM TipoSolicitacao t ORDER BY t.especie")
    List<TipoSolicitacao> findAllOrderByEspecie();
}