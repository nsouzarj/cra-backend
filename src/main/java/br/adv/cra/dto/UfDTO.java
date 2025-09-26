package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UfDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String sigla;
    private String nome;
}