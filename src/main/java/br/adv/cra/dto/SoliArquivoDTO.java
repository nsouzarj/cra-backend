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
    
    // Storage location: "local" or "google_drive"
    @JsonProperty("storageLocation")
    private String storageLocation = "local";
    
    // Google Drive file ID (when stored in Google Drive)
    @JsonProperty("googleDriveFileId")
    private String googleDriveFileId;
    
    // User ID who owns this file (for Google Drive access)
    @JsonProperty("userId")
    private Long userId;
}