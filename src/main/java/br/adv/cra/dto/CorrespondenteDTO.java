package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorrespondenteDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nome;
    private String cpfcnpj;
    private String oab;
    private String emailprimario;
    private String emailsecundario;
    private String telefoneprimario;
    private String telefonesecundario;
    private String telefonecelularprimario;
    private String telefonecelularsecundario;
    private String tipocorrepondente;
    private LocalDateTime datacadastro;
    private boolean ativo;
    private boolean aplicaregra1;
    private boolean aplicaregra2;
}