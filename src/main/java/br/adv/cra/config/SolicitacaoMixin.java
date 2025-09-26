package br.adv.cra.config;

import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Comarca;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitacaoMixin {
    
    @JsonProperty("processo")
    @JsonIgnoreProperties({"solicitacoes", "hibernateLazyInitializer", "handler"})
    private Processo processo;
    
    @JsonProperty("comarca")
    @JsonIgnoreProperties({"solicitacoes", "hibernateLazyInitializer", "handler"})
    private Comarca comarca;
}