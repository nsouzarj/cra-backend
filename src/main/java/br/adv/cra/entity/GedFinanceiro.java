package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gedfinanceiro")
@SequenceGenerator(name = "seqgedfin", sequenceName = "idgedinf", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GedFinanceiro implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqgedfin")
	private Long idgedfin;
	
	private Float valorbruto;
	private Float volumetria;
	private Float percvolumetria;
	private Float desconto;
	private Float percdesconto;
	private String notadedebito;
	private Float valor;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime data;
	
	private String uf;
	
	@ManyToOne
	private Correspondente correspondente;
	
	private String nomearquivo;
	private String link;
}