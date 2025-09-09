package br.adv.cra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Login é obrigatório")
    private String login;
    
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}