package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "endereco")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idendereco")
    private Long id;
    
    private String logradouro;
    
    private String numero;
    
    private String complemento;
    
    private String bairro;
    
    private String cidade;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uf_id")
    private Uf uf;
    
    private String cep;
    
    private String observacao;
}