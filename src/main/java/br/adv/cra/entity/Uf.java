package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "uf")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Uf implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "iduf")
	private Long id;
	
	@Column(length = 2)
	private String sigla;
	
	@Column(length = 60)
	private String nome;
}
