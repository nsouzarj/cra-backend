package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "tiposolicitacao")
@SequenceGenerator(name = "seqtiposolicitacao", sequenceName = "idtiposolicitacao", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitacao implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtiposolicitacao")
	private Long idtiposolicitacao;
	
	private String especie;
	private String descricao;
	private String tipo;
	private Boolean visualizar;
}
