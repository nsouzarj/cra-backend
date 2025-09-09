package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "logsistema")
@SequenceGenerator(name = "seqlog", sequenceName = "idlog", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSistema implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqlog")
	private Long idlog;
	
	private String usuario;
	private Long idsolicitacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datalog;
	
	private String tela;
	
	@Column(length=3000,columnDefinition="Text")
	private String descricao;
}