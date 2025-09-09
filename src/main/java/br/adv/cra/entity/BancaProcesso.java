package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "bancaprocesso")
@SequenceGenerator(name = "seqbanca", sequenceName = "idbanca", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancaProcesso implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqbanca")
	private Long idbanca;
	
	private String banca;
	private String descricao;
	private boolean ativa;
	private String email; //Email da banca 
	private String emailgestordabanca; //Email do gestor da banca
}