package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gedcolaborador")
@SequenceGenerator(name = "gedcolaborador", sequenceName = "idgedarquivo", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoColaborador {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gedcolaborador")
	private Long idgedarquivo;
	
	private String descricao;
	private String nome;
	private String obsged;
	private String mesanocompetencia;
	private String link;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datainclusao;
	
	@ManyToOne
	private Correspondente corresp;
}
