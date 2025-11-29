package org.example.smartshop.dto.response;

import lombok.*;
import org.example.smartshop.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientStatisticsResponse {

    private String nom;
    private String email;
    private CustomerTier niveauFidelite;
    private Integer nombreCommandes;
    private BigDecimal montantCumule;
    private BigDecimal montantMoyenParCommande;
    private LocalDateTime datePremiereCommande;
    private LocalDateTime dateDerniereCommande;
}
