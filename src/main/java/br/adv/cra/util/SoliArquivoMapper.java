package br.adv.cra.util;

import br.adv.cra.dto.SoliArquivoDTO;
import br.adv.cra.entity.SoliArquivo;

public class SoliArquivoMapper {
    
    public static SoliArquivoDTO toDTO(SoliArquivo soliArquivo) {
        if (soliArquivo == null) {
            return null;
        }
        
        SoliArquivoDTO dto = new SoliArquivoDTO();
        dto.setId(soliArquivo.getId());
        dto.setIdSolicitacao(soliArquivo.getSolicitacao() != null ? soliArquivo.getSolicitacao().getIdsolicitacao() : null);
        dto.setNomearquivo(soliArquivo.getNomearquivo());
        dto.setDataInclusao(soliArquivo.getDatainclusao());
        dto.setOrigem(soliArquivo.getOrigem());
        dto.setAtivo(soliArquivo.isAtivo());
        dto.setCaminhoRelativo(soliArquivo.getCaminhorelativo());
        dto.setStorageLocation(soliArquivo.getStorageLocation());
        dto.setGoogleDriveFileId(soliArquivo.getGoogleDriveFileId());
        dto.setUserId(soliArquivo.getUserId());
        
        return dto;
    }
    
    public static SoliArquivo toEntity(SoliArquivoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        SoliArquivo entity = new SoliArquivo();
        entity.setId(dto.getId());
        // Note: solicitacao relationship should be set separately if needed
        entity.setNomearquivo(dto.getNomearquivo());
        entity.setDatainclusao(dto.getDataInclusao());
        entity.setOrigem(dto.getOrigem());
        entity.setAtivo(dto.isAtivo());
        entity.setCaminhorelativo(dto.getCaminhoRelativo());
        entity.setStorageLocation(dto.getStorageLocation());
        entity.setGoogleDriveFileId(dto.getGoogleDriveFileId());
        entity.setUserId(dto.getUserId());
        
        return entity;
    }
}