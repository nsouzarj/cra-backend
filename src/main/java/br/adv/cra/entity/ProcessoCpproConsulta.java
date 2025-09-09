package br.adv.cra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoCpproConsulta implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String numerinical;
	private String clienteprincipal;
	private String orgaoinicial;
	private String contrarioprincipal;
	private String tipoacaorito;
	private String codigopasta;
	private String processoeletronico;
	private String pastacliente;
	private String responsavel;
}
