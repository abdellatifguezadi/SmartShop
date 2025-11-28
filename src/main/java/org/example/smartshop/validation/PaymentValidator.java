package org.example.smartshop.validation;

import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.enums.PaymentMethod;
import org.example.smartshop.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PaymentValidator {

    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000.00");

    public void validatePayment(PaymentRequest request) {
        if (request.getTypePaiement() == PaymentMethod.ESPECES) {
            if (request.getMontant().compareTo(ESPECES_LIMIT) > 0) {
                throw new BusinessException("Le montant en especes ne peut pas depasser 20,000 DH (Art. 193 CGI)");
            }
        }

        if (request.getTypePaiement() == PaymentMethod.CHEQUE) {
            if (request.getNumeroCheque() == null || request.getNumeroCheque().isBlank()) {
                throw new BusinessException("Le numero de cheque est obligatoire");
            }
            if (request.getBanque() == null || request.getBanque().isBlank()) {
                throw new BusinessException("La banque est obligatoire pour un cheque");
            }
        }

        if (request.getTypePaiement() == PaymentMethod.VIREMENT) {
            if (request.getReferenceVirement() == null || request.getReferenceVirement().isBlank()) {
                throw new BusinessException("La reference de virement est obligatoire");
            }
        }
    }
}