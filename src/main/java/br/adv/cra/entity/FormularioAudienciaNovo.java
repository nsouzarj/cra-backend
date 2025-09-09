package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity(name="formaudnovo")
@SequenceGenerator(name = "seqformnovo", sequenceName = "idformnovo", allocationSize = 1, initialValue = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioAudienciaNovo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqformnovo")
	private Long idformnovo;
	private boolean assitidoadvogado; //O autor est assistido por por advogado
	private boolean autorcompaudiencia; //Autor compareceu a audiencia 
	private boolean encerrarformulario; //Encerra o formualrio nao presente mas nada
	private String telautor;//Telefone do autor
	private String emaildoautor; //Email do autor
	private boolean aijdesignada;// Aij desiganda
	private boolean acordorealizado; //Acordo realizado 
	private String oabexadverso; //Oab do exadverso
	private String telexadverso;// Telefone do exaadverso
	private String emailexadverso; //Email do exadverso
}