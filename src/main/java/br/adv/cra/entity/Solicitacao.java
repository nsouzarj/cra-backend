package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import br.adv.cra.config.CustomLocalDateTimeDeserializer;
import br.adv.cra.config.CustomLocalDateTimeSerializer;

@Entity
@Table(name = "solicitacao")
@SequenceGenerator(name = "seqsol", sequenceName = "idsolicitacao", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqsol")
    private Long idsolicitacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime datasolicitacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime dataconclusao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime dataagendamento;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime dataprazo;
    
    @Column(length = 30)
    private String numero;
    
    @Column(length = 30)
    private String vara;
    
    // Replacing the string field with a proper relationship to Comarca entity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idcomarca")
    @JsonIgnoreProperties("solicitacoes")
    private Comarca comarca;
    
    @Column(length = 30)
    private String uf;
    
    @Column(length = 100)
    private String requerente;
    
    @Column(length = 100)
    private String requerido;
    
    @Column(length = 600, columnDefinition = "Text")
    private String observacao;
    
    @Column(length = 600, columnDefinition = "Text")
    private String instrucoes;
    
    @Column(length = 600, columnDefinition = "Text")
    private String complemento;
    
    @Column(length = 600, columnDefinition = "Text")
    private String justificativa;
    
    @Column(length = 600, columnDefinition = "Text")
    private String tratposaudiencia; //Tratamento pos audiencia
    
    @Column(length = 30)
    private String numcontrole;
    
    private boolean tempreposto;
    
    private boolean convolada;
    
    @Column(nullable = true, length = 10)
    private String horaudiencia;
    
    @Column(nullable = true, length = 20)
    private String statusexterno; // CONFIRMAR - REJEITAR

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idprocesso")
    @JsonIgnoreProperties("solicitacoes")
    private Processo processo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idrenumeracao")
    @JsonIgnoreProperties("solicitacoes")
    private Renumeracao renumeracao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idstatus")
    @JsonIgnoreProperties("solicitacoes")
    private StatusSolicitacao statusSolicitacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idformulario")
    @JsonIgnoreProperties("solicitacoes")
    private FormularioAudiencia formularioAudiencia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idbanca")
    @JsonIgnoreProperties("solicitacoes")
    private BancaProcesso bancaProcesso;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idenviosolicitacao")
    @JsonIgnoreProperties("solicitacoes")
    private Enviosolicitacao enviosolicitacao;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idrecibo")
    @JsonIgnoreProperties("solicitacoes")
    private ReciboPagamento reciboPagamento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idusuario")
    @JsonIgnoreProperties("solicitacoes")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idaudiinterna")
    @JsonIgnoreProperties("solicitacoes")
    private AuditoriaInterna auditoriaInterna;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idtiposolicitacao")
    @JsonIgnoreProperties("solicitacoes")
    private TipoSolicitacao tipoSolicitacao;
    
    // Adding the relationship with Correspondente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idcorrespondente")
    @JsonIgnoreProperties("solicitacoes")
    private Correspondente correspondente;
    
    private float valor;
    
    private float valordaalcada;
    
    private String emailenvio;
    
    private String pago;
    
    private Integer grupo;
    
    @Column(nullable = true)
    private boolean propostaacordo;
    
    @Column(nullable = true)
    private boolean audinterna;
    
    @Column(length = 1)
    private String lide;
    
    private Integer avaliacaonota;
    
    private String textoavaliacao;
    
    // Adding the inverse relationship with Historico
    @OneToMany(mappedBy = "solicitacao", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Historico> historicos;
    
    // Adding the inverse relationship with SoliArquivo
    @OneToMany(mappedBy = "solicitacaoPossuiArquivo.solicitacao", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SoliArquivo> soliArquivos;
    
    // Custom setter for compatibility with existing code
    public void setId(Long id) {
        this.idsolicitacao = id;
    }
    
    // Custom getter for compatibility with existing code
    public Long getId() {
        return this.idsolicitacao;
    }
    
    // Custom setter for referenciasolicitacao (assuming this is a field that should exist)
    public void setReferenciasolicitacao(int referencia) {
        // This is a placeholder implementation
        // If there's a specific field for this, it should be set here
    }
}