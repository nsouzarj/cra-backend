package br.adv.cra.repository;

import br.adv.cra.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByLogin(String login);
    
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
    
    List<Usuario> findByTipo(Integer tipo);
    
    List<Usuario> findByAtivoTrue();
    
    List<Usuario> findByAtivoFalse();
    
    @Query("SELECT u FROM Usuario u WHERE u.nomecompleto LIKE %:nome%")
    List<Usuario> findByNomeCompletoContaining(@Param("nome") String nome);
    
    @Query("SELECT u FROM Usuario u WHERE u.emailprincipal = :email OR u.emailsecundario = :email OR u.emailresponsavel = :email")
    List<Usuario> findByAnyEmail(@Param("email") String email);
    
    boolean existsByLogin(String login);
}