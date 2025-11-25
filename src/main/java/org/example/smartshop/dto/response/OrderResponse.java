package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private Long clientId;
    private String clientNom;
    private LocalDateTime dateCreation;
    private BigDecimal sousTotal;
    private BigDecimal montantRemise;
    private BigDecimal montantHTApresRemise;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;
    private BigDecimal montantRestant;
    private String codePromo;
    private OrderStatus statut;
    private List<OrderItemResponse> items;
    private List<PaymentResponse> payments;
}
