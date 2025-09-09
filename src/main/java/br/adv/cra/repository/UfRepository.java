package br.adv.cra.repository;

import br.adv.cra.entity.Uf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UfRepository extends JpaRepository<Uf, Long> {
    
    Optional<Uf> findBySigla(String sigla);
    
    @Query("SELECT u FROM Uf u WHERE u.nome LIKE %:nome%")
    List<Uf> findByNomeContaining(@Param("nome") String nome);
    
    boolean existsBySigla(String sigla);
    
    @Query("SELECT u FROM Uf u ORDER BY u.nome")
    List<Uf> findAllOrderByNome();
}