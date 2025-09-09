package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComarcaCorrespondente implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idcorrespondente")
	private Correspondente correspondente;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idcomarca")
	private Comarca comarca;
}