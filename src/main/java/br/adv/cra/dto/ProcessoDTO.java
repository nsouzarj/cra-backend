package br.adv.cra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoDTO {
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
    private Long orgaoId;
    private Integer numorgao;
    private String proceletronico;
    private Integer quantsoli;
    private Date datadistribuicao;
    private String observacao;
}