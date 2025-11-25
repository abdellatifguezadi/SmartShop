package org.example.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.smartshop.enums.OrderStatus;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderUpdateRequest {
    
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Format du code promo invalide (PROMO-XXXX)")
    private String codePromo;
    
    @DecimalMin(value = "0.0", message = "Le taux de TVA doit être positif")
    @DecimalMax(value = "100.0", message = "Le taux de TVA doit être entre 0 et 100")
    private BigDecimal tauxTVA;
    
    private OrderStatus statut;
}
