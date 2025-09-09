/**
 * 
 */
package br.adv.cra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nelson
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoCPJ implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String numprocesso;
	private String numficha;
	private String numprocessopes;
	private String nomeautor;
	private String nomereu;
	private String siglaloc;
	private String locnumero;
	private String siglaintegracao;
	private String numerointegracao;
	private String juizo;
	private String acao;
	private String ojnumero;
	private String ojsigla;
	private String assuntodoprocesso;
	private String proceletronico;

}
