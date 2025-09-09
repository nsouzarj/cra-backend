package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "banco")
@SequenceGenerator(name = "seqbanco", sequenceName = "idbanco", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banco implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqbanco")
	private Long idbanco;
	
	private String codbanco;
	private String agencia;
	private String banco;
	private String contacorrente;
	
	@ManyToOne
	private Correspondente correspondente;
}