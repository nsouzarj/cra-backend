package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "solicitacao_possui_arquivo")
@IdClass(SolicitacaoPossuiArquivoId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoPossuiArquivo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idsolicitacao")
    private Solicitacao solicitacao;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idarquivoanexo")
    private SolicitacaoAnexo solicitacaoAnexo;
}