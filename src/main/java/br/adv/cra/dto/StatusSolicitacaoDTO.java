package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusSolicitacaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idstatus;
    private String status;
}