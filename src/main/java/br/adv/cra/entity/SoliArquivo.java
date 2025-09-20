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
	
	// Storage location: "local" or "google_drive"
	@Column(name = "storage_location", length = 20)
	private String storageLocation = "local";
	
	// Google Drive file ID (when stored in Google Drive)
	@Column(name = "google_drive_file_id", length = 255)
	private String googleDriveFileId;
	
	// User ID who owns this file (for Google Drive access)
	@Column(name = "user_id")
	private Long userId;
}