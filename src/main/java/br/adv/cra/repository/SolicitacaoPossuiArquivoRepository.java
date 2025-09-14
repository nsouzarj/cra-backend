package br.adv.cra.repository;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.SolicitacaoAnexo;
import br.adv.cra.entity.SolicitacaoPossuiArquivo;
import br.adv.cra.entity.SolicitacaoPossuiArquivoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoPossuiArquivoRepository extends JpaRepository<SolicitacaoPossuiArquivo, SolicitacaoPossuiArquivoId> {
    List<SolicitacaoPossuiArquivo> findBySolicitacao(Solicitacao solicitacao);
    List<SolicitacaoPossuiArquivo> findBySolicitacaoAnexo(SolicitacaoAnexo solicitacaoAnexo);
    void deleteBySolicitacaoAnexo(SolicitacaoAnexo solicitacaoAnexo);
}