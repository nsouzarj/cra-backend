package br.adv.cra;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Solicitacao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestSerialization {
    public static void main(String[] args) {
        try {
            // Create a correspondente
            Correspondente correspondente = new Correspondente();
            correspondente.setId(1L);
            correspondente.setNome("Test Correspondente");
            correspondente.setCpfcnpj("12345678901");
            correspondente.setOab("123456SP");
            correspondente.setDatacadastro(LocalDateTime.now());
            correspondente.setAtivo(true);
            
            // Create a solicitacao
            Solicitacao solicitacao = new Solicitacao();
            solicitacao.setId(1L);
            solicitacao.setReferenciasolicitacao(1001);
            solicitacao.setDatasolictacao(LocalDateTime.now());
            solicitacao.setCorrespondente(correspondente);
            
            // Add solicitacao to correspondente
            List<Solicitacao> solicitacoes = new ArrayList<>();
            solicitacoes.add(solicitacao);
            correspondente.setSolicitacoes(solicitacoes);
            
            // Test serialization
            ObjectMapper mapper = new ObjectMapper();
            
            // Test serializing correspondente
            String correspondenteJson = mapper.writeValueAsString(correspondente);
            System.out.println("Correspondente JSON: " + correspondenteJson);
            
            // Test serializing solicitacao
            String solicitacaoJson = mapper.writeValueAsString(solicitacao);
            System.out.println("Solicitacao JSON: " + solicitacaoJson);
            
            System.out.println("Serialization test passed!");
        } catch (JsonProcessingException e) {
            System.err.println("Serialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}