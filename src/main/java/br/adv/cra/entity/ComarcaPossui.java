package br.adv.cra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "comarcapossui")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComarcaPossui implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ComarcaCorrespondente comarcaCorrespondente;
	
	private boolean inativado;
}