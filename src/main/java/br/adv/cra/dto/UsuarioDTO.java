package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nomecompleto;
    private String login;
    private String emailprincipal;
    private String emailsecundario;
    private String emailresponsavel;
    private Integer tipo;
    private boolean ativo;
    private LocalDateTime dataentrada;
}