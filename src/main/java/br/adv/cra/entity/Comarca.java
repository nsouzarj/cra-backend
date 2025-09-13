package br.adv.cra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "comarca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comarca implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomarca")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "uf_id", nullable = false)
    private Uf uf;

    private boolean ativo;
    
    // Adding the inverse relationship with Solicitacao
    @OneToMany(mappedBy = "comarca", fetch = FetchType.LAZY)
    private List<Solicitacao> solicitacoes;
}