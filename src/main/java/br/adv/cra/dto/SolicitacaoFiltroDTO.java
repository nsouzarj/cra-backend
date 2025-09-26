package br.adv.cra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoFiltroDTO {
    // Basic pagination
    private int page = 0;
    private int size = 10;
    private String sortBy = "datasolicitacao";
    private String direction = "DESC";
    
    // Filter fields
    private Long comarcaId;
    private Long correspondenteId;
    private Long processoId;
    private Long usuarioId;
    private Long statusId;
    private String status; // Status by name
    private Long tipoSolicitacaoId;
    private Long grupo;
    private String statusExterno;
    private String texto; // For text search in observacao or instrucoes
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Boolean pago;
    private Boolean concluida;
    private Boolean atrasada;
    
    // Additional filters
    private String numero;
    private String vara;
    private String requerente;
    private String requerido;
    private String uf;
    
    // Constructor with just pagination
    public SolicitacaoFiltroDTO(int page, int size, String sortBy, String direction) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.direction = direction;
    }
}