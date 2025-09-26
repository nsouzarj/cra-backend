package br.adv.cra.controller;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Comarca;
import br.adv.cra.service.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-solicitacao")
public class TestSolicitacaoController {
    
    @Autowired
    private SolicitacaoService solicitacaoService;
    
    @PostMapping("/test-save")
    public Solicitacao testSave(@RequestBody Solicitacao solicitacao) {
        // Try to save and return the saved entity
        return solicitacaoService.salvar(solicitacao);
    }
    
    @GetMapping("/sample")
    public Solicitacao getSample() {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setNumero("TEST-001");
        solicitacao.setVara("1ª Vara Cível");
        
        Processo processo = new Processo();
        processo.setNumeroprocesso("1234567-89.2023.8.19.0001");
        solicitacao.setProcesso(processo);
        
        Comarca comarca = new Comarca();
        comarca.setNome("Comarca de Teste");
        solicitacao.setComarca(comarca);
        
        return solicitacao;
    }
}