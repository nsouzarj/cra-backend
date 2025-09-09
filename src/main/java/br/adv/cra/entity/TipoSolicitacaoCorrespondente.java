package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "tiposolicitacaocorrespondente")
@SequenceGenerator(name = "seqtiposolicitacaocorrespondente", sequenceName = "idtiposolicitacaocorrespondente", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitacaoCorrespondente implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtiposolicitacaocorrespondente")
	private Long idtiposolicitacaocorrespondente;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idcorrespondente")
	private Correspondente correspondente;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idsolicitacao")
	private TipoSolicitacao tipoSolicitacao;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idenvio")
	private Envio envio;
}