package org.example.smartshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientUpdateRequest {
    
    private String nom;
    
    @Email(message = "Format email invalide")
    private String email;

    private String username;
    
    private String password;
}
