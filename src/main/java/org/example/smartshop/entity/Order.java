package org.example.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreation;

    private BigDecimal sousTotal;
    private BigDecimal montantRemise;
    private BigDecimal montantHTApresRemise;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;
    private BigDecimal montantRestant;

    private String codePromo;

    @Builder.Default
    private BigDecimal tauxTVA = new BigDecimal("20");

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (tauxTVA == null) {
            tauxTVA = new BigDecimal("20");
        }
    }
}
