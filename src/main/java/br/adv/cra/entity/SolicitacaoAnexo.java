package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "arquivosanexo")
@SequenceGenerator(name = "seqanexo", sequenceName = "idarquivoanexo", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAnexo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqanexo")
	private Long idarquivoanexo;
	
	@Lob
	@Transient
	private byte[] arquivo;
	
	private String linkarquivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datasolicitacao;
	
	private String tipoarquivo;
	private String nomearquivo;
	private String operacao; // Entrada ou Saida
	private Integer origemarq;   // 1 - Siegecol 2-Cprpo
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario")
	private Usuario usuario;
}
