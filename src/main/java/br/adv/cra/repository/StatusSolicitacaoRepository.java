package br.adv.cra.repository;

import br.adv.cra.entity.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusSolicitacaoRepository extends JpaRepository<StatusSolicitacao, Long> {
    
    @Query("SELECT s FROM StatusSolicitacao s WHERE s.status LIKE CONCAT('%', :status, '%')")
    List<StatusSolicitacao> findByStatusContaining(@Param("status") String status);
    
    @Query("SELECT s FROM StatusSolicitacao s WHERE s.status = :status")
    Optional<StatusSolicitacao> findByStatus(@Param("status") String status);
    
    @Query("SELECT s FROM StatusSolicitacao s ORDER BY s.status ASC")
    List<StatusSolicitacao> findAllOrderByStatus();
}