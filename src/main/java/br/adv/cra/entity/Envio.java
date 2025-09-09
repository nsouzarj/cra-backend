package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Classe de teste
 * @author Nelson
 *
 */

@Entity
@Table(name = "envio")
@SequenceGenerator(name = "seqenvio", sequenceName = "idenvio", initialValue = 1, allocationSize = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqenvio")
	private Long idenvio;
	
	private String descricao;
}
