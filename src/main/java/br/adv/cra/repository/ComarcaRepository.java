package br.adv.cra.repository;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComarcaRepository extends JpaRepository<Comarca, Long> {
    
    List<Comarca> findByUf(Uf uf);
    
    @Query("SELECT c FROM Comarca c WHERE c.nome LIKE %:nome%")
    List<Comarca> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT c FROM Comarca c ORDER BY c.nome")
    List<Comarca> findAllOrderByNome();
    
    @Query("SELECT c FROM Comarca c WHERE c.uf.sigla = :sigla ORDER BY c.nome")
    List<Comarca> findByUfSiglaOrderByNome(@Param("sigla") String sigla);
}