package org.example.smartshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequest {
    
    @NotBlank(message = "Username est obligatoire")
    private String username;
    
    @NotBlank(message = "Password est obligatoire")
    private String password;
}
