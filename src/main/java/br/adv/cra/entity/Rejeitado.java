package br.adv.cra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rejeitado {
	private Long idsolicitacao;
	private String numeroprocesso;
	private String pasta;
	private String motivo;
	private String documento;
	private String anexadopor;
	private LocalDateTime datanexado;
	private LocalDateTime rejeitadoem;
	private LocalDateTime datasolicitacao;
	private String quemrejeitou;
}
