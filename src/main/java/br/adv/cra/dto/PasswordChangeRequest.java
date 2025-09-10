package br.adv.cra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Long id;
    
    @NotBlank(message = "Nova senha é obrigatória")
    private String novaSenha;
}