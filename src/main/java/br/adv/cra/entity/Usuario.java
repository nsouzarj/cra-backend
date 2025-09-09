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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Long id;
    
    private String login;
    
    private String senha;
    
    private String nomecompleto;
    
    private String emailprincipal;
    
    private String emailsecundario;
    
    private String emailresponsavel;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idcorrespondente")
    @JsonIgnoreProperties("usuarios")
    private Correspondente correspondente;
    
    // Custom setter to handle deserialization when only ID is provided
    @JsonSetter("correspondente")
    public void setCorrespondenteFromId(Object correspondenteObj) {
        if (correspondenteObj instanceof Number) {
            // If it's a number, create a Correspondente with just the ID
            Long id = ((Number) correspondenteObj).longValue();
            Correspondente corr = new Correspondente();
            corr.setId(id);
            this.correspondente = corr;
        } else if (correspondenteObj instanceof Correspondente) {
            // If it's already a Correspondente object, use it directly
            this.correspondente = (Correspondente) correspondenteObj;
        } else {
            // For other cases, set to null
            this.correspondente = null;
        }
    }
    
    private Integer tipo; // 1-Adm, 2-Advgodado, 3-Correspondente
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataentrada;
    
    private boolean ativo;
    
    // Adding the inverse relationship with Solicitacao
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Solicitacao> solicitacoes;
}