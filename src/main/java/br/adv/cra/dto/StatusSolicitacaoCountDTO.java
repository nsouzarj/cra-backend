package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusSolicitacaoCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String status;
    private Long count;
}