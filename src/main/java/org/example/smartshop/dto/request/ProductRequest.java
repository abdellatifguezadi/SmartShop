package org.example.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductRequest {
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;
    
    private String description;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.00",inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prixUnitaire;
    
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stockDisponible;
}
