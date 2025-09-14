package br.adv.cra.repository;

import br.adv.cra.entity.SolicitacaoAnexo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoAnexoRepository extends JpaRepository<SolicitacaoAnexo, Long> {
}