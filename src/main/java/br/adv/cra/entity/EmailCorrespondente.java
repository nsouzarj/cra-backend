package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "emailscorrespondente")
@SequenceGenerator(initialValue = 1, name = "seqemail", sequenceName = "idemail", allocationSize = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCorrespondente implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqemail")
	private Long idemail;
	
	@Column(length = 100)
	private String email;
	
	@ManyToOne
	private Correspondente correspondente;
}
