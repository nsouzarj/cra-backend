package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "perfilusuario")
@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seqperfil", sequenceName = "idperfil")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuario implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqperfil")
	private Long idperfilusuario;
	
	private String perfil;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario")
	private Usuario usuario;
}
