package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Arquivos do cpro
 * 
 * @author Nelson
 * Esta classe e a tabela dos arquivos salos do cppro
 *
 */

@Entity
@Table(name = "anexocprosalvo")
@SequenceGenerator(name = "seqcprposalvo", sequenceName = "idarqcpprosalvo", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoAnexoCPROSalvo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcprposalvo")
	private Long idarqcpprosalvo;
	
	private Long idArquivo;
	private Long idContexto;
	private Long idsolicitacao;
	private String PastaDoProcesso;
	private String NomeDocumento;
	private String CaminhoGed;
	private String AnexadoPor;
	private String TipoDocumento;
	private String Descricao;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime AnexadoEm;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime BaixadoEm;
	
	private boolean baixado;
	private boolean rejeitado;
	
	@ManyToOne
	private Usuario rejeitadopor; //No tem funcionalidade
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime datareijeicao;
	
	@Column(columnDefinition = "TEXT")
	private String motivodarejeicao;
}
