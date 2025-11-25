package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientStatisticsResponse {
    
    private Long clientId;
    private String nom;
    private String email;
    private CustomerTier niveauFidelite;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime datePremiereCommande;
    private LocalDateTime dateDerniereCommande;
    private List<OrderSummaryResponse> historiqueCommandes;
}
