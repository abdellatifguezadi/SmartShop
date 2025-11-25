package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientResponse {
    
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    private CustomerTier niveauFidelite;
    private Integer totalOrders;
    private BigDecimal totalSpent;
}
