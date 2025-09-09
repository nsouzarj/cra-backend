package br.adv.cra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Login é obrigatório")
    @Size(min = 3, max = 50, message = "Login deve ter entre 3 e 50 caracteres")
    private String login;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String senha;
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 255, message = "Nome completo deve ter no máximo 255 caracteres")
    private String nomeCompleto;
    
    @Email(message = "Email deve ser válido")
    private String emailPrincipal;
    
    @Email(message = "Email secundário deve ser válido")
    private String emailSecundario;
    
    @Email(message = "Email responsável deve ser válido")
    private String emailResponsavel;
    
    @NotNull(message = "Tipo é obrigatório")
    private Integer tipo; // 1-Admin, 2-Advogado, 3-Correspondente
    
    // Optional field to associate an existing correspondent when registering a "correspondente" user
    private Long correspondenteId;
}