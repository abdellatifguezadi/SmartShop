package org.example.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.smartshop.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentRequest {
    
    @NotNull(message = "L'ID de la commande est obligatoire")
    private Long orderId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;
    
    @NotNull(message = "Le type de paiement est obligatoire")
    private PaymentMethod typePaiement;
    
    private String numeroCheque;
    private String banque;
    private LocalDate dateEcheance;
    
    private String referenceVirement;
}
