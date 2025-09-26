package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitacaoCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long tipoSolicitacaoId;
    private String tipoSolicitacaoEspecie;
    private Long count;
}