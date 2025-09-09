package br.adv.cra.repository;

import br.adv.cra.entity.Correspondente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorrespondenteRepository extends JpaRepository<Correspondente, Long> {
    
    List<Correspondente> findByAtivoTrue();
    
    List<Correspondente> findByAtivoFalse();
    
    @Query("SELECT c FROM Correspondente c WHERE c.nome LIKE CONCAT('%', :nome, '%')")
    List<Correspondente> findByNomeContaining(@Param("nome") String nome);
    
    Optional<Correspondente> findByCpfcnpj(String cpfCnpj);
    
    Optional<Correspondente> findByOab(String oab);
    
    List<Correspondente> findByTipocorrepondente(String tipoCorrespondente);
    
    @Query("SELECT c FROM Correspondente c WHERE c.emailprimario = :email OR c.emailsecundario = :email")
    List<Correspondente> findByAnyEmail(@Param("email") String email);
    
    List<Correspondente> findByAplicaregra1True();
    
    List<Correspondente> findByAplicaregra2True();
    
    boolean existsByCpfcnpj(String cpfCnpj);
    
    boolean existsByOab(String oab);
    
    // Adding method to fetch correspondente with solicitacoes
    @Query("SELECT c FROM Correspondente c LEFT JOIN FETCH c.solicitacoes WHERE c.id = :id")
    Optional<Correspondente> findByIdWithSolicitacoes(@Param("id") Long id);
}