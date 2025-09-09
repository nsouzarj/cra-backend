package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico")
@SequenceGenerator(name = "seqhist", sequenceName = "idhistorico", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historico implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqhist")
	private Long idhistorico;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datahistorico;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idsolicitacao")
	private Solicitacao solicitacao;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idstatus")
	private StatusSolicitacao statusSolicitacao;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idrenumeracao")
	private Renumeracao renumeracao;
	
	@Column(length = 600, columnDefinition = "Text")
	private String textohistorico;
}
