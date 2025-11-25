package org.example.smartshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @Pattern(regexp = "[0-9]{10}", message = "Le numéro de téléphone doit contenir 10 chiffres")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
    
    @NotBlank(message = "Le username est obligatoire")
    private String username;
    
    @NotBlank(message = "Le password est obligatoire")
    private String password;
}
