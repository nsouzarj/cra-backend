package br.adv.cra.controller;

import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.service.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping("/date-filter")
    public ResponseEntity<String> debugDateFilter(@RequestBody SolicitacaoFiltroDTO filtro) {
        try {
            System.out.println("=== DEBUG DATE FILTER ===");
            System.out.println("Data Inicio: " + filtro.getDataInicio());
            System.out.println("Data Fim: " + filtro.getDataFim());
            System.out.println("Data Conclusao Inicio: " + filtro.getDataConclusaoInicio());
            System.out.println("Data Conclusao Fim: " + filtro.getDataConclusaoFim());
            System.out.println("Data Prazo Inicio: " + filtro.getDataPrazoInicio());
            System.out.println("Data Prazo Fim: " + filtro.getDataPrazoFim());
            
            Sort sort = Sort.by(Sort.Direction.fromString(filtro.getDirection()), filtro.getSortBy());
            PageRequest pageable = PageRequest.of(filtro.getPage(), filtro.getSize(), sort);
            
            Page<Solicitacao> result = solicitacaoService.buscarAvancado(filtro, pageable);
            
            System.out.println("Found " + result.getTotalElements() + " solicitacoes");
            
            return ResponseEntity.ok("Received dates: inicio=" + filtro.getDataInicio() + ", fim=" + filtro.getDataFim() + 
                                   ". Found " + result.getTotalElements() + " records.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/test-date-parse")
    public ResponseEntity<String> testDateParse(
            @RequestParam String dateString) {
        try {
            // This will help us understand what format your frontend is sending
            return ResponseEntity.ok("Received date string: " + dateString);
        } catch (Exception e) {
            return ResponseEntity.ok("Error parsing date: " + e.getMessage());
        }
    }
}