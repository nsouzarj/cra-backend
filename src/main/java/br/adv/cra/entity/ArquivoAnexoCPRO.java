package br.adv.cra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Arquivos do cpro
 * 
 * @author Nelson
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoAnexoCPRO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long idArquivo;
	private Long idContexto;
	private String PastaDoProcesso;
	private String NomeDocumento;
	private String CaminhoGed;
	private String AnexadoPor;
	private String TipoDocumento;
	private String Descricao;
	private LocalDateTime AnexadoEm;
	private boolean rejeitado;
	private String motivodarejeicao;
}
