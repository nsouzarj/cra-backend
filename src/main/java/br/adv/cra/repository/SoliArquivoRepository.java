package br.adv.cra.repository;

import br.adv.cra.entity.SoliArquivo;
import br.adv.cra.entity.Solicitacao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoliArquivoRepository extends JpaRepository<SoliArquivo, Long> {
    List<SoliArquivo> findBySolicitacao(Solicitacao solicitacao);
    List<SoliArquivo> findBySolicitacao(Solicitacao solicitacao, Sort sort);
    List<SoliArquivo> findBySolicitacaoIdsolicitacao(Long idSolicitacao);
    List<SoliArquivo> findBySolicitacaoIdsolicitacao(Long idSolicitacao, Sort sort);
}