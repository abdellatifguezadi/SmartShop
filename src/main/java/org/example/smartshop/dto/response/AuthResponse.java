package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.UserRole;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    
    private Long userId;
    private String username;
    private UserRole role;
    private String message;
}
