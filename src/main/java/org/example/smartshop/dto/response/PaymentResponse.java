package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.PaymentMethod;
import org.example.smartshop.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentResponse {
    
    private Long id;
    private Integer numeroPaiement;
    private BigDecimal montant;
    private PaymentMethod typePaiement;
    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;
    private PaymentStatus statut;
    private String numeroRecu;
    private String numeroCheque;
    private String referenceVirement;
    private String banque;
    private LocalDate dateEcheance;
}
