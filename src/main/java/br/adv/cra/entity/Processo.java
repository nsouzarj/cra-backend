package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "processo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Processo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprocesso")
    private Long id;
    
    private String numeroprocesso;
    
    private String numeroprocessopesq;
    
    private String parte;
    
    private String adverso;
    
    private String posicao;
    
    private String status;
    
    private String cartorio;
    
    @Column(length = 600, columnDefinition = "Text")
    private String assunto;
    
    private String localizacao;
    
    private String numerointegracao;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "comarca_idcomarca")
    private Comarca comarca;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "orgao_idorgao")
    private Orgao orgao;
    
    private Integer numorgao;
    
    private String proceletronico;
    
    private Integer quantsoli;

    private Date datadistribuicao;
    @Column(length = 1000, columnDefinition = "Text")
    private String observacao;
    
    @Transient
    private Integer totalfeita;
}