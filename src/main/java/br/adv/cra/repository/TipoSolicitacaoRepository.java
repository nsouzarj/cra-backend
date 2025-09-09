package br.adv.cra.repository;

import br.adv.cra.entity.TipoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoSolicitacaoRepository extends JpaRepository<TipoSolicitacao, Long> {
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.especie LIKE %:especie%")
    List<TipoSolicitacao> findByEspecieContaining(@Param("especie") String especie);
    
    @Query("SELECT t FROM TipoSolicitacao t WHERE t.descricao LIKE %:descricao%")
    List<TipoSolicitacao> findByDescricaoContaining(@Param("descricao") String descricao);
    
    List<TipoSolicitacao> findByTipo(String tipo);
    
    List<TipoSolicitacao> findByVisualizar(Boolean visualizar);
    
    @Query("SELECT t FROM TipoSolicitacao t ORDER BY t.especie")
    List<TipoSolicitacao> findAllOrderByEspecie();
}