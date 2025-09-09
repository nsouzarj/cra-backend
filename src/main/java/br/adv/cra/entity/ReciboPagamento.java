package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "recibopagamento")
@SequenceGenerator(initialValue = 1, name = "seqpag", sequenceName = "seqpag", allocationSize = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReciboPagamento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqpag")
	private Long idrecibo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datafechamento;
	
	private String anotacao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario")
	private Usuario usuario;
}
