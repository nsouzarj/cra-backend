package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "arquivosanexados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoliArquivo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idsolicitacao")
	private Solicitacao solicitacao;
	
	@Column(length = 255)
	private String nomearquivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datainclusao;
	
	@Column(length = 500)
	private String caminhofisico;
	
	@Column(length = 100)
	private String origem;
	
	private boolean ativo;
	
	@Column(length = 500)
	private String caminhorelativo;
	
	// Removed driveFileId field as Google Drive integration is no longer used
}