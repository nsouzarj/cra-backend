package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String numeroprocesso;
    private String numeroprocessopesq;
    private String parte;
    private String adverso;
    private String posicao;
    private String status;
    private String cartorio;
    private String assunto;
    private String localizacao;
    private String numerointegracao;
    private Long comarcaId;
    private String comarcaNome;
    private Long orgaoId;
    private String orgaoDescricao;
    private Integer numorgao;
    private String proceletronico;
    private Integer quantsoli;
    private Date datadistribuicao;
    private String observacao;
}