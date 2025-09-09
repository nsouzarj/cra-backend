package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "preposto")
@SequenceGenerator(initialValue = 1, name = "seqpreposto", sequenceName = "seqpreposto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preposto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqpreposto")
	private Long idpresposto;
	
	private String nomecompleto;
	private String cpf;
	private String identidade;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datanascimento;
	
	private String observacao;
	
	@OneToOne
	private Endereco enderecos;
}
