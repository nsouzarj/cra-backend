package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idsolicitacao;
    private LocalDateTime datasolicitacao;
    private LocalDateTime dataconclusao;
    private LocalDateTime dataagendamento;
    private LocalDateTime dataprazo;
    private String numero;
    private String vara;
    private Long comarcaId;
    private String comarcaNome;
    private String uf;
    private String requerente;
    private String requerido;
    private String observacao;
    private String instrucoes;
    private String complemento;
    private String justificativa;
    private String tratposaudiencia;
    private String numcontrole;
    private boolean tempreposto;
    private boolean convolada;
    private String horaudiencia;
    private String statusexterno;
    private Long processoId;
    private String processoNumero;
    private Long statusSolicitacaoId;
    private String statusSolicitacaoStatus;
    private Long usuarioId;
    private String usuarioNome;
    private Long tipoSolicitacaoId;
    private String tipoSolicitacaoEspecie;
    private Long correspondenteId;
    private String correspondenteNome;
    private float valor;
    private float valordaalcada;
    private String emailenvio;
    private String pago;
    private Integer grupo;
    private boolean propostaacordo;
    private boolean audinterna;
    private String lide;
    private Integer avaliacaonota;
    private String textoavaliacao;
}