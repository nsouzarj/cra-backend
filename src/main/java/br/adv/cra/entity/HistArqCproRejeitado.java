package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "histarqcpprorej")
@SequenceGenerator(name = "seqhistcprorej", sequenceName = "idarqcpprorej", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistArqCproRejeitado implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqhistcprorej")
	private Long idarqcpprorej;
	
	private Long idarquivocppro;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime rejeitadoem;
	
	@Column(columnDefinition="TEXT")
    private String motivo;
}
