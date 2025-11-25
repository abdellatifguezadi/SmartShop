package org.example.smartshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductResponse {
    
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixUnitaire;
    private Integer stockDisponible;
}
