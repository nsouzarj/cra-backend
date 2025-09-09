package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "histarqcppro")
@SequenceGenerator(name = "seqhistcpro", sequenceName = "idarqcppro", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistArqCpro {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqhistcpro")
	private Long idarqcppro;
	
	@OneToOne
	private SolicitacaoAnexo solicitacaoAnexo;
	
	private Long idarquivocppro;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime salvoem;
}
