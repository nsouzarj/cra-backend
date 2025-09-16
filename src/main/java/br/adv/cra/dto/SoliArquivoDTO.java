package br.adv.cra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoliArquivoDTO {
    private Long id;
    
    @JsonProperty("idSolicitacao")
    private Long idSolicitacao;
    
    private String nomearquivo;
    
    @JsonProperty("dataInclusao")
    private LocalDateTime dataInclusao;
    
    private String origem;
    
    private boolean ativo;
    
    @JsonProperty("caminhoRelativo")
    private String caminhoRelativo;
}