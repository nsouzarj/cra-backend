package br.adv.cra.controller;

import br.adv.cra.dto.PaginatedResponseDTO;
import br.adv.cra.dto.SolicitacaoDTO;
import br.adv.cra.dto.SolicitacaoFiltroDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.StatusSolicitacao;
import br.adv.cra.entity.Usuario;
import br.adv.cra.service.SolicitacaoService;
import br.adv.cra.service.StatusSolicitacaoService;
import br.adv.cra.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import br.adv.cra.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing requests.
 * 
 * This controller provides full CRUD operations for requests and specialized
 * endpoints for request management. Requests represent tasks or actions that
 * need to be processed in the system.
 * 
 * Base URL: /api/solicitacoes
 */
@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {
    
    private final SolicitacaoService solicitacaoService;
    private final StatusSolicitacaoService statusSolicitacaoService;
    private final UsuarioService usuarioService; // Added to fetch usuario by ID
    
    /**
     * Creates a new request.
     * 
     * @param solicitacao The request information to create
     * @return The created request with HTTP 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<Solicitacao> criar(@Valid @RequestBody Solicitacao solicitacao) {
        try {
            Solicitacao novaSolicitacao = solicitacaoService.salvar(solicitacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaSolicitacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing request.
     * 
     * @param id The ID of the request to update
     * @param solicitacao The updated request information
     * @return The updated request, or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Solicitacao> atualizar(@PathVariable Long id, @Valid @RequestBody Solicitacao solicitacao) {
        try {
            // Check if the solicitacao exists
            if (!solicitacaoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Set the ID to ensure we're updating the correct entity
            solicitacao.setId(id);
            
            // Ensure the datasolicitacao is not null to prevent issues
            Solicitacao solicitacaoAtualizada = solicitacaoService.atualizar(solicitacao);
            return ResponseEntity.ok(solicitacaoAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<Solicitacao> setStatus(@PathVariable Long id, @PathVariable Long statusId) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Check if the current user has permission to change the status
            Solicitacao solicitacao = solicitacaoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            
            // If the solicitacao is already concluded, only ADVOGADO (lawyer) and ADMIN can change the status
            if (solicitacao.getStatusSolicitacao() != null && 
                "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
                
                boolean hasPermission = false;
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_ADVOGADO".equals(role)) {
                        hasPermission = true;
                        break;
                    }
                }
                
                if (!hasPermission) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            Solicitacao updatedSolicitacao = solicitacaoService.setStatus(id, statusId);
            return ResponseEntity.ok(updatedSolicitacao);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/status-nome/{statusNome}")
    public ResponseEntity<Solicitacao> setStatusPorNome(@PathVariable Long id, @PathVariable String statusNome) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Check if the current user has permission to change the status
            Solicitacao solicitacao = solicitacaoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            
            // If the solicitacao is already concluded, only ADVOGADO (lawyer) and ADMIN can change the status
            if (solicitacao.getStatusSolicitacao() != null && 
                "Concluída".equals(solicitacao.getStatusSolicitacao().getStatus())) {
                
                boolean hasPermission = false;
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_ADVOGADO".equals(role)) {
                        hasPermission = true;
                        break;
                    }
                }
                
                if (!hasPermission) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            Solicitacao updatedSolicitacao = solicitacaoService.setStatusPorNome(id, statusNome);
            return ResponseEntity.ok(updatedSolicitacao);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all requests with pagination.
     * 
     * @return Page of requests
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarTodas(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all requests DTO with pagination.
     * 
     * @return Page of requests DTO
     */
    @GetMapping("/list/dto")
    public ResponseEntity<PaginatedResponseDTO<SolicitacaoDTO>> listarTodasDTO(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<SolicitacaoDTO> solicitacoes = solicitacaoService.listarTodasDTO(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<SolicitacaoDTO> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists pending requests with pagination.
     * 
     * @return Page of pending requests
     */
    @GetMapping("/pendentes")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarPendentes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarPendentes(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists completed requests with pagination.
     * 
     * @return Page of completed requests
     */
    @GetMapping("/concluidas")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarConcluidas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataconclusao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarConcluidas(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists overdue requests with pagination.
     * 
     * @return Page of overdue requests
     */
    @GetMapping("/atrasadas")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarAtrasadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataprazo") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarAtrasadas(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists paid requests with pagination.
     * 
     * @return Page of paid requests
     */
    @GetMapping("/pagas")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarPagas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarPagas(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists unpaid requests with pagination.
     * 
     * @return Page of unpaid requests
     */
    @GetMapping("/nao-pagas")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> listarNaoPagas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.listarNaoPagas(pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by user ID, where the user is associated with the correspondente of the requests.
     * 
     * @param usuarioId The user ID to search for
     * @return Page of requests for the specified user's correspondente
     */
    @GetMapping("/usuario/{usuarioId}/correspondente")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorUsuarioCorrespondente(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // First, fetch the usuario by ID
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Check if the usuario has a correspondente
            if (usuario.getCorrespondente() == null) {
                // Return empty page if no correspondente
                Page<Solicitacao> emptyPage = Page.empty();
                long totalTableElements = solicitacaoService.contarTodas();
                PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(emptyPage, totalTableElements);
                return ResponseEntity.ok(response);
            }
            
            // Fetch solicitacoes by the usuario's correspondente
            Correspondente correspondente = usuario.getCorrespondente();
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorCorrespondente(correspondente, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by user with pagination.
     * 
     * @param usuarioId The user ID to search for
     * @return Page of requests for the specified user
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // Create a Usuario object with the ID to pass to the service
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorUsuario(usuario, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by correspondente with pagination.
     * 
     * @param correspondenteId The correspondente ID to search for
     * @return Page of requests for the specified correspondente
     */
    @GetMapping("/correspondente/{correspondenteId}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorCorrespondente(
            @PathVariable Long correspondenteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // Create a Correspondente object with the ID to pass to the service
            Correspondente correspondente = new Correspondente();
            correspondente.setId(correspondenteId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorCorrespondente(correspondente, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches requests by date range with pagination.
     * 
     * @param inicio The start date/time
     * @param fim The end date/time
     * @return Page of requests within the specified date range
     */
    @GetMapping("/buscar/periodo")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorPeriodo(inicio, fim, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches requests by text (partial match) with pagination.
     * 
     * @param texto The text to search for
     * @return Page of matching requests
     */
    @GetMapping("/buscar/texto")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorTexto(
            @RequestParam String texto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorTexto(texto, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by court (comarca) and correspondente with pagination.
     * 
     * @param comarcaId The court ID to search for
     * @param correspondenteId The correspondente ID to search for
     * @return Page of requests in the specified court and correspondente
     */
    @GetMapping("/buscar/comarca/{comarcaId}/correspondente/{correspondenteId}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorComarcaECorrespondente(
            @PathVariable Long comarcaId,
            @PathVariable Long correspondenteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // Create a Comarca object with the ID to pass to the service
            br.adv.cra.entity.Comarca comarca = new br.adv.cra.entity.Comarca();
            comarca.setId(comarcaId);
            
            // Create a Correspondente object with the ID to pass to the service
            br.adv.cra.entity.Correspondente correspondente = new br.adv.cra.entity.Correspondente();
            correspondente.setId(correspondenteId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorComarcaECorrespondente(comarca, correspondente, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Solicitacao> emptyPage = Page.empty();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds requests by court (comarca) with pagination.
     * 
     * @param comarcaId The court ID to search for
     * @return Page of requests in the specified court
     */
    @GetMapping("/buscar/comarca/{comarcaId}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorComarca(
            @PathVariable Long comarcaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // Create a Comarca object with the ID to pass to the service
            br.adv.cra.entity.Comarca comarca = new br.adv.cra.entity.Comarca();
            comarca.setId(comarcaId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorComarca(comarca, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Solicitacao> emptyPage = Page.empty();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds requests by court (comarca) and correspondente with pagination using query parameters.
     * This endpoint is designed to match the frontend request format.
     * 
     * @param comarcaId The court ID to search for
     * @param correspondenteId The correspondente ID to search for
     * @return Page of requests in the specified court and correspondente
     */
    @GetMapping("/buscar/comarca")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorComarcaECorrespondenteQuery(
            @RequestParam Long comarcaId,
            @RequestParam Long correspondenteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            // Create a Comarca object with the ID to pass to the service
            br.adv.cra.entity.Comarca comarca = new br.adv.cra.entity.Comarca();
            comarca.setId(comarcaId);
            
            // Create a Correspondente object with the ID to pass to the service
            br.adv.cra.entity.Correspondente correspondente = new br.adv.cra.entity.Correspondente();
            correspondente.setId(correspondenteId);
            
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorComarcaECorrespondente(comarca, correspondente, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Solicitacao> emptyPage = Page.empty();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Advanced search endpoint for requests with multiple combined filters.
     * 
     * @param filtro The filter criteria
     * @return Page of requests matching the filter criteria
     */
    @PostMapping("/buscar/avancado")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarAvancado(@RequestBody SolicitacaoFiltroDTO filtro) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(filtro.getDirection()), filtro.getSortBy());
            Pageable pageable = PageRequest.of(filtro.getPage(), filtro.getSize(), sort);
            
            // Use the new service method for advanced search
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarAvancado(filtro, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Page<Solicitacao> emptyPage = Page.empty();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(emptyPage, 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Finds requests by group with pagination.
     * 
     * @param grupo The group ID to search for
     * @return Page of requests in the specified group
     */
    @GetMapping("/buscar/grupo/{grupo}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorGrupo(
            @PathVariable Integer grupo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorGrupo(grupo, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds requests by external status with pagination.
     * 
     * @param status The external status to search for
     * @return Page of requests with the specified external status
     */
    @GetMapping("/buscar/status/{status}")
    public ResponseEntity<PaginatedResponseDTO<Solicitacao>> buscarPorStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "datasolicitacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Solicitacao> solicitacoes = solicitacaoService.buscarPorStatusExterno(status, pageable);
            long totalTableElements = solicitacaoService.contarTodas();
            PaginatedResponseDTO<Solicitacao> response = new PaginatedResponseDTO<>(solicitacoes, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as completed.
     * 
     * @param id The ID of the request to complete
     * @param observacao Optional observation note
     * @return The completed request, or error response
     */
    @PutMapping("/{id}/concluir")
    public ResponseEntity<Solicitacao> concluir(@PathVariable Long id, @RequestBody(required = false) String observacao) {
        try {
            Solicitacao solicitacao = solicitacaoService.concluir(id, observacao);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as paid.
     * 
     * @param id The ID of the request to mark as paid
     * @return The updated request, or error response
     */
    @PutMapping("/{id}/marcar-pago")
    public ResponseEntity<Solicitacao> marcarComoPago(@PathVariable Long id) {
        try {
            Solicitacao solicitacao = solicitacaoService.marcarComoPago(id);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marks a request as unpaid.
     * 
     * @param id The ID of the request to mark as unpaid
     * @return The updated request, or error response
     */
    @PutMapping("/{id}/marcar-nao-pago")
    public ResponseEntity<Solicitacao> marcarComoNaoPago(@PathVariable Long id) {
        try {
            Solicitacao solicitacao = solicitacaoService.marcarComoNaoPago(id);
            return ResponseEntity.ok(solicitacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Counts pending requests.
     * 
     * @return The count of pending requests
     */
    @GetMapping("/estatisticas/pendentes")
    public ResponseEntity<Long> contarPendentes() {
        try {
            Long count = solicitacaoService.contarPendentes();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a request by ID.
     * 
     * @param id The ID of the request to retrieve
     * @return The request if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Solicitacao> buscarPorId(@PathVariable Long id) {
        try {
            return solicitacaoService.buscarPorId(id)
                    .map(solicitacao -> ResponseEntity.ok(solicitacao))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a request.
     * 
     * @param id The ID of the request to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            solicitacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}