package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderSummaryResponse {
    
    private Long id;
    private LocalDateTime dateCreation;
    private BigDecimal totalTTC;
    private OrderStatus statut;
}
