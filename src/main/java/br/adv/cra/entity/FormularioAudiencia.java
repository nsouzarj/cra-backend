package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "formularioaudiencia")
@SequenceGenerator(name = "seqformulario", sequenceName = "idformulario", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioAudiencia implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqformulario")
	private Long idformulario;
	
	private String nomeadvogado;
	private String numoab;
	private String telefoneadvogado;
	//@Email
	private String emailadvogado;
	private String advogadoadverso;
	private String numoabadverso;
	
	private String telefoneadvadervoso;
	private String telefonecel1;
	private String telefonecel2;
	//@Email
	private String emailadvadverso;
	private boolean defesagenerica;
	private boolean contraproposta;
	private boolean aijdesiginada;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime dataaij;
	
	private double valorproposta;
	private double valorcontraproposta;
	
    @Column(length = 1000, columnDefinition = "Text")
	private String obrigacaoafazer;
	
    @Column(length = 1000, columnDefinition = "Text")
	private String informecontraproposta;
	
    @Column(length = 1000, columnDefinition = "Text")
	private String objetodalide;
	
    @Column(length = 1000, columnDefinition = "Text")
	private String estrategiadefesa;
	
    @Column(length = 1000, columnDefinition = "Text")
	private String informeacontecimento;
	
	private Integer nivel;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime dataformulario;
	
	private boolean acordorealizado;
	private double valoracordo;
}
