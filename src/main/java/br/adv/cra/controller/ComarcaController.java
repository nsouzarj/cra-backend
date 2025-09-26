package br.adv.cra.controller;

import br.adv.cra.dto.ComarcaDTO;
import br.adv.cra.dto.PaginatedResponseDTO;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Uf;
import br.adv.cra.service.ComarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing court districts (Comarcas).
 * 
 * This controller provides endpoints for court district operations,
 * including CRUD operations and specialized search capabilities.
 * Court districts are administrative divisions of Brazilian states.
 * 
 * Base URL: /api/comarcas
 */
@RestController
@RequestMapping("/api/comarcas")
@RequiredArgsConstructor
@Tag(name = "comarca", description = "Operações relacionadas às comarcas")
public class ComarcaController {
    
    private final ComarcaService comarcaService;
    
    /**
     * Lists all court districts with pagination.
     * 
     * @return Page of court districts
     */
    @GetMapping
    @Operation(summary = "Listar comarcas", description = "Lista todas as comarcas com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista de comarcas retornada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<PaginatedResponseDTO<Comarca>> listarTodas(
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Comarca> comarcas = comarcaService.listarTodasOrdenadas(pageable);
            long totalTableElements = comarcaService.contarTodas();
            PaginatedResponseDTO<Comarca> response = new PaginatedResponseDTO<>(comarcas, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lists all court districts DTO with pagination.
     * 
     * @return Page of court districts DTO
     */
    @GetMapping("/list/dto")
    @Operation(summary = "Listar comarcas DTO", description = "Lista todas as comarcas como DTO com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista de comarcas DTO retornada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComarcaDTO.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<PaginatedResponseDTO<ComarcaDTO>> listarTodasDTO(
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ComarcaDTO> comarcas = comarcaService.listarTodasDTO(pageable);
            long totalTableElements = comarcaService.contarTodas();
            PaginatedResponseDTO<ComarcaDTO> response = new PaginatedResponseDTO<>(comarcas, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a court district by ID.
     * 
     * @param id The ID of the court district to retrieve
     * @return The court district if found, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter comarca por ID", description = "Obtém uma comarca específica pelo ID")
    @ApiResponse(responseCode = "200", description = "Comarca encontrada", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "404", description = "Comarca não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Comarca> buscarPorId(
            @Parameter(description = "ID da comarca a ser obtida") 
            @PathVariable Long id) {
        try {
            return comarcaService.buscarPorId(id)
                    .map(comarca -> ResponseEntity.ok(comarca))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Creates a new court district.
     * 
     * @param comarca The court district information to create
     * @return The created court district with HTTP 201 status, or error response
     */
    @PostMapping
    @Operation(summary = "Criar comarca", description = "Cria uma nova comarca")
    @ApiResponse(responseCode = "201", description = "Comarca criada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Comarca> criar(
            @Parameter(description = "Dados da comarca a ser criada") 
            @Valid @RequestBody Comarca comarca) {
        try {
            Comarca novaComarca = comarcaService.salvar(comarca);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaComarca);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates an existing court district.
     * 
     * @param id The ID of the court district to update
     * @param comarca The updated court district information
     * @return The updated court district, or error response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar comarca", description = "Atualiza uma comarca existente")
    @ApiResponse(responseCode = "200", description = "Comarca atualizada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "404", description = "Comarca não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Comarca> atualizar(
            @Parameter(description = "ID da comarca a ser atualizada") 
            @PathVariable Long id, 
            @Parameter(description = "Dados atualizados da comarca") 
            @Valid @RequestBody Comarca comarca) {
        try {
            if (!comarcaService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            comarca.setId(id);
            Comarca comarcaAtualizada = comarcaService.atualizar(comarca);
            return ResponseEntity.ok(comarcaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Searches court districts by name (partial match) with pagination.
     * 
     * @param nome The name to search for
     * @return Page of matching court districts
     */
    @GetMapping("/buscar/nome")
    @Operation(summary = "Buscar comarcas por nome", description = "Busca comarcas pelo nome com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista de comarcas encontradas", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<PaginatedResponseDTO<Comarca>> buscarPorNome(
            @Parameter(description = "Texto parcial do nome da comarca") 
            @RequestParam String nome,
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Comarca> comarcas = comarcaService.buscarPorNome(nome, pageable);
            long totalTableElements = comarcaService.contarTodas();
            PaginatedResponseDTO<Comarca> response = new PaginatedResponseDTO<>(comarcas, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds court districts by state (UF) ID with pagination.
     * 
     * @param ufId The state ID to search for
     * @return Page of court districts in the specified state
     */
    @GetMapping("/buscar/uf/{ufId}")
    @Operation(summary = "Buscar comarcas por ID do estado", description = "Busca comarcas por ID do estado com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista de comarcas encontradas", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<PaginatedResponseDTO<Comarca>> buscarPorUf(
            @Parameter(description = "ID do estado") 
            @PathVariable Long ufId,
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Use the proper repository method that handles pagination correctly
            Page<Comarca> comarcas = comarcaService.buscarPorUfId(ufId, pageable);
            long totalTableElements = comarcaService.contarTodas();
            PaginatedResponseDTO<Comarca> response = new PaginatedResponseDTO<>(comarcas, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Finds court districts by state abbreviation (sigla) with pagination.
     * 
     * @param sigla The state abbreviation to search for
     * @return Page of court districts in the specified state
     */
    @GetMapping("/buscar/uf/sigla/{sigla}")
    @Operation(summary = "Buscar comarcas por sigla do estado", description = "Busca comarcas por sigla do estado com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista de comarcas encontradas", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comarca.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<PaginatedResponseDTO<Comarca>> buscarPorUfSigla(
            @Parameter(description = "Sigla do estado") 
            @PathVariable String sigla,
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Comarca> comarcas = comarcaService.buscarPorUfSigla(sigla, pageable);
            long totalTableElements = comarcaService.contarTodas();
            PaginatedResponseDTO<Comarca> response = new PaginatedResponseDTO<>(comarcas, totalTableElements);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deletes a court district.
     * 
     * @param id The ID of the court district to delete
     * @return 204 No Content if successful, 404 if not found, or error response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir comarca", description = "Exclui uma comarca existente")
    @ApiResponse(responseCode = "204", description = "Comarca excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Comarca não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da comarca a ser excluída") 
            @PathVariable Long id) {
        try {
            comarcaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets the total count of all court districts.
     * 
     * @return Total count of court districts
     */
    @GetMapping("/count")
    @Operation(summary = "Contar total de comarcas", description = "Retorna o número total de comarcas")
    @ApiResponse(responseCode = "200", description = "Total de comarcas retornado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Long> contarTodas() {
        try {
            long count = comarcaService.contarTodas();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets the count of court districts matching a name search.
     * 
     * @param nome The name to search for
     * @return Count of matching court districts
     */
    @GetMapping("/count/nome")
    @Operation(summary = "Contar comarcas por nome", description = "Retorna o número de comarcas que correspondem ao nome")
    @ApiResponse(responseCode = "200", description = "Total de comarcas encontradas retornado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Long> contarPorNome(
            @Parameter(description = "Texto parcial do nome da comarca") 
            @RequestParam String nome) {
        try {
            long count = comarcaService.contarPorNome(nome);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets the count of court districts in a state by ID.
     * 
     * @param ufId The state ID to search for
     * @return Count of court districts in the specified state
     */
    @GetMapping("/count/uf/{ufId}")
    @Operation(summary = "Contar comarcas por ID do estado", description = "Retorna o número de comarcas no estado especificado")
    @ApiResponse(responseCode = "200", description = "Total de comarcas no estado retornado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Long> contarPorUf(
            @Parameter(description = "ID do estado") 
            @PathVariable Long ufId) {
        try {
            long count = comarcaService.contarPorUfId(ufId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets the count of court districts in a state by sigla.
     * 
     * @param sigla The state abbreviation to search for
     * @return Count of court districts in the specified state
     */
    @GetMapping("/count/uf/sigla/{sigla}")
    @Operation(summary = "Contar comarcas por sigla do estado", description = "Retorna o número de comarcas na sigla do estado especificada")
    @ApiResponse(responseCode = "200", description = "Total de comarcas na sigla do estado retornado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Long> contarPorUfSigla(
            @Parameter(description = "Sigla do estado") 
            @PathVariable String sigla) {
        try {
            long count = comarcaService.contarPorUfSigla(sigla);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}