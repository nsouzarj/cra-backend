package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "enviode")
@SequenceGenerator(name = "seqenviosoli", sequenceName = "idenviosolicitacao", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enviosolicitacao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqenviosoli")
	private Long idenviosolicitacao;
	
	private String tipoenvio;
}
