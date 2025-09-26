package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComarcaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nome;
    private Long ufId;
    private String ufSigla;
    private String ufNome;
    private boolean ativo;
}