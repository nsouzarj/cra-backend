package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "arquivoanexado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoliArquivo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private SolicitacaoPossuiArquivoId solicitacaoPossuiArquivoId;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @MapsId("solicitacao")
    @JoinColumn(name = "idsolicitacao")
    private Solicitacao solicitacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("solicitacaoAnexo")
    @JoinColumn(name = "idarquivoanexo")
    private SolicitacaoAnexo solicitacaoAnexo;
}