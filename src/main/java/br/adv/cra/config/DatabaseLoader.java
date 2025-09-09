package br.adv.cra.config;

import br.adv.cra.entity.Orgao;
import br.adv.cra.repository.OrgaoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DatabaseLoader {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);
    
    private final OrgaoRepository orgaoRepository;
    
    @PostConstruct
    public void loadData() {
        log.info("Loading initial data...");
        
        // Check if we already have the specific orgaos by description to avoid duplicates
        List<String> orgaoDescriptions = Arrays.asList(
            "Tribunal de Justiça",
            "Tribunal Regional Federal",
            "Superior Tribunal de Justiça",
            "Tribunal Superior do Trabalho",
            "Tribunal Regional do Trabalho"
        );
        
        // Check if any of the required orgaos are missing
        boolean allOrgaosExist = orgaoDescriptions.stream()
            .allMatch(desc -> !orgaoRepository.findByDescricaoContaining(desc).isEmpty());
        
        if (!allOrgaosExist) {
            log.info("Some orgaos are missing, checking which ones to load...");
            
            // Only create orgaos that don't already exist
            List<Orgao> orgaosToCreate = orgaoDescriptions.stream()
                .filter(desc -> orgaoRepository.findByDescricaoContaining(desc).isEmpty())
                .map(desc -> new Orgao(null, desc))
                .toList();
            
            if (!orgaosToCreate.isEmpty()) {
                List<Orgao> savedOrgaos = orgaoRepository.saveAll(orgaosToCreate);
                log.info("Loaded {} new orgaos", savedOrgaos.size());
            } else {
                log.info("All required orgaos already exist, no new data loaded.");
            }
        } else {
            log.info("All required orgaos already exist, skipping data load. Total count: {}", orgaoRepository.count());
        }
    }
}