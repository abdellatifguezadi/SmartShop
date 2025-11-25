package org.example.smartshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;
    
    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Valid
    private List<OrderItemRequest> items;
    
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Format du code promo invalide (PROMO-XXXX)")
    private String codePromo;
    
    @DecimalMin(value = "0.0", message = "Le taux de TVA doit être positif")
    @DecimalMax(value = "100.0", message = "Le taux de TVA doit être entre 0 et 100")
    private BigDecimal tauxTVA;
}
