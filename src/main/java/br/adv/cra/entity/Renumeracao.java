package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "renumeracao")
@SequenceGenerator(name = "seqrenumeracao", sequenceName = "idrenumeracao", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Renumeracao implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqrenumeracao")
	private Long idrenumeracao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoSolicitacaoCorrespondente tipoSolicitacaoCorrespondente;
	
	// private ComarcaCorrespondente comarcaCorrespondente;
	private float valor;
	private boolean ativo;
}