package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitacaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idtiposolicitacao;
    private String especie;
    private String descricao;
    private String tipo;
    private Boolean visualizar;
}