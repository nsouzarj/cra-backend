package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "smsalvo")
@SequenceGenerator(name = "seqsms", sequenceName = "idsms", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsSalvo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long idsms;
	
	@ManyToOne
	private Usuario usuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime dataenvio;
	
	private String numero;
	private String menssagem;
}