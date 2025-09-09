package br.adv.cra.repository;

import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Uf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    
    List<Endereco> findByUf(Uf uf);
    
    @Query("SELECT e FROM Endereco e WHERE e.cidade LIKE %:cidade%")
    List<Endereco> findByCidadeContaining(@Param("cidade") String cidade);
    
    @Query("SELECT e FROM Endereco e WHERE e.bairro LIKE %:bairro%")
    List<Endereco> findByBairroContaining(@Param("bairro") String bairro);
    
    List<Endereco> findByCep(String cep);
    
    @Query("SELECT e FROM Endereco e WHERE e.logradouro LIKE %:logradouro%")
    List<Endereco> findByLogradouroContaining(@Param("logradouro") String logradouro);
}