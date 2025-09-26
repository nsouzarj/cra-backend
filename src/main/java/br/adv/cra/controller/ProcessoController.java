package br.adv.cra.controller;

import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Orgao;
import br.adv.cra.entity.Processo;
import br.adv.cra.service.ProcessoService;
import br.adv.cra.dto.ProcessoDTO;
import br.adv.cra.dto.PaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing legal processes.
 * 
 * This controller provides full CRUD operations for legal processes and advanced
 * search capabilities. Processes represent legal cases managed in the system.
 * 
 * Base URL: /api/processos
 */
@RestController
@RequestMapping("/api/processos")
@RequiredArgsConstructor
public class ProcessoController {
    
    private final ProcessoService processoService;
    
    /**
     * Creates a new process.
     * 
     * @param processo The process information to create
     * @return The created process with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Processo processo) {
        try {
            if (processoService.existeNumeroProcesso(processo.getNumeroprocesso())) {
                return ResponseEntity.badRequest().body("Número de processo já existe");
            }
            Processo novoProcesso = processoService.salvar(processo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProcesso);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar processo: " + e.getMessage());
        }
    }
    
    /**
     * Creates a new process using DTO.
     * 
     * @param processoDTO The process information to create
     * @return The created process with HTTP 201 status, or error response
     */
    @PostMapping("/dto")
    public ResponseEntity<?> criarComDTO(@Valid @RequestBody ProcessoDTO processoDTO) {
        try {
            Processo novoProcesso = processoService.salvarComDTO(processoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProcesso);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar processo: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing process.
     * 
     * @param id The ID of the process to update
     * @param processo The updated process information
     * @return The updated process, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Processo processo) {
        try {
            if (!processoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (processoService.existeNumeroProcessoParaOutroProcesso(processo.getNumeroprocesso(), id)) {
                return ResponseEntity.badRequest().body("Número de processo já existe para outro processo");
            }
            
            processo.setId(id);
            Processo processoAtualizado = processoService.atualizar(processo);
            return ResponseEntity.ok(processoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar processo: " + e.getMessage());
        }
    }
    
    /**
     * Lists all processes with pagination.
     * 
     * @return Page of processes
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<Processo>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.listarTodos(pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all processes DTO with pagination.
     * 
     * @return Page of processes DTO
     */
    @GetMapping("/list/dto")
    public ResponseEntity<PaginatedResponseDTO<ProcessoDTO>> listarTodosDTO(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProcessoDTO> processos = processoService.listarTodosDTO(pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<ProcessoDTO> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a process by ID.
     * 
     * @param id The ID of the process to retrieve
     * @return The process if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return processoService.buscarPorId(id)
                    .map(processo -> ResponseEntity.ok(processo))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar processo: " + e.getMessage());
        }
    }
    
    /**
     * Finds process by process number.
     * 
     * @param numeroProcesso The process number to search for
     * @return The process if found, or 404 if not found
     */
    @GetMapping("/buscar/numero/{numeroProcesso}")
    public ResponseEntity<?> buscarPorNumeroProcesso(@PathVariable String numeroProcesso) {
        try {
            return processoService.buscarPorNumeroProcesso(numeroProcesso)
                    .map(processo -> ResponseEntity.ok(processo))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar processo: " + e.getMessage());
        }
    }
    
    /**
     * Searches processes by process number (partial match) with pagination.
     * 
     * @param numero The process number to search for
     * @return Page of matching processes
     */
    @GetMapping("/buscar/numero-pesquisa")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorNumeroProcessoPesquisa(
            @RequestParam String numero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorNumeroProcessoPesquisa(numero, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Searches processes by party name (partial match) with pagination.
     * 
     * @param parte The party name to search for
     * @return Page of matching processes
     */
    @GetMapping("/buscar/parte")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorParte(
            @RequestParam String parte,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorParte(parte, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Searches processes by opposing party name (partial match) with pagination.
     * 
     * @param adverso The opposing party name to search for
     * @return Page of matching processes
     */
    @GetMapping("/buscar/adverso")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorAdverso(
            @RequestParam String adverso,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorAdverso(adverso, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds processes by status with pagination.
     * 
     * @param status The process status to search for
     * @return Page of processes with the specified status
     */
    @GetMapping("/buscar/status/{status}")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorStatus(status, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Searches processes by subject (partial match) with pagination.
     * 
     * @param assunto The subject to search for
     * @return Page of matching processes
     */
    @GetMapping("/buscar/assunto")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorAssunto(
            @RequestParam String assunto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorAssunto(assunto, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Searches processes by electronic process (partial match) with pagination.
     * 
     * @param processoEletronico The electronic process to search for
     * @return Page of matching processes
     */
    @GetMapping("/buscar/processo-eletronico")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorProcessoEletronico(
            @RequestParam String processoEletronico,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorProcessoEletronico(processoEletronico, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds processes by court with pagination.
     * 
     * @param comarcaId The court ID to search for
     * @return Page of processes in the specified court
     */
    @GetMapping("/buscar/comarca/{comarcaId}")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorComarca(
            @PathVariable Long comarcaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            // Create a Comarca object with the ID to pass to the service
            Comarca comarca = new Comarca();
            comarca.setId(comarcaId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorComarca(comarca, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds processes by court with pagination.
     * 
     * @param orgaoId The court ID to search for
     * @return Page of processes in the specified court
     */
    @GetMapping("/buscar/orgao/{orgaoId}")
    public ResponseEntity<PaginatedResponseDTO<Processo>> buscarPorOrgao(
            @PathVariable Long orgaoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "numeroprocesso") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            // Create an Orgao object with the ID to pass to the service
            Orgao orgao = new Orgao();
            orgao.setId(orgaoId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Processo> processos = processoService.buscarPorOrgao(orgao, pageable);
            long totalTableElements = processoService.contarTodos();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(processos, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Processo> emptyPage = Page.empty();
            PaginatedResponseDTO<Processo> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Counts processes by status.
     * 
     * @param status The process status to count
     * @return The count of processes with the specified status
     */
    @GetMapping("/estatisticas/status/{status}")
    public ResponseEntity<?> contarPorStatus(@PathVariable String status) {
        try {
            Long count = processoService.contarPorStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao contar processos: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a process.
     * 
     * @param id The ID of the process to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            processoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar processo: " + e.getMessage());
        }
    }
}