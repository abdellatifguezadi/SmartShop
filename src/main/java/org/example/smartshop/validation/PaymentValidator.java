package org.example.smartshop.validation;

import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.enums.PaymentMethod;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PaymentValidator {
    
    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000.00");
    
    public void validatePayment(PaymentRequest request) {
        if (request.getTypePaiement() == PaymentMethod.ESPECES) {
            if (request.getMontant().compareTo(ESPECES_LIMIT) > 0) {
                throw new IllegalArgumentException("Le montant en espèces ne peut pas dépasser 20,000 DH (Art. 193 CGI)");
            }
        }
        
        if (request.getTypePaiement() == PaymentMethod.CHEQUE) {
            if (request.getNumeroCheque() == null || request.getNumeroCheque().isBlank()) {
                throw new IllegalArgumentException("Le numéro de chèque est obligatoire");
            }
            if (request.getBanque() == null || request.getBanque().isBlank()) {
                throw new IllegalArgumentException("La banque est obligatoire pour un chèque");
            }
        }
        
        if (request.getTypePaiement() == PaymentMethod.VIREMENT) {
            if (request.getReferenceVirement() == null || request.getReferenceVirement().isBlank()) {
                throw new IllegalArgumentException("La référence de virement est obligatoire");
            }
        }
    }
}
