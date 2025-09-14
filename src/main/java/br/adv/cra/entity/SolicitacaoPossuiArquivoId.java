package br.adv.cra.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoPossuiArquivoId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long solicitacao;
    private Long solicitacaoAnexo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoPossuiArquivoId that = (SolicitacaoPossuiArquivoId) o;
        return Objects.equals(solicitacao, that.solicitacao) &&
               Objects.equals(solicitacaoAnexo, that.solicitacaoAnexo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solicitacao, solicitacaoAnexo);
    }
}