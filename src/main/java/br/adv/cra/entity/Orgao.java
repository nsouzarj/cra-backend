package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "orgao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orgao implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idorgao", nullable = false)
    private Long id;
    
    @Column(length = 50)
    private String descricao;
}
