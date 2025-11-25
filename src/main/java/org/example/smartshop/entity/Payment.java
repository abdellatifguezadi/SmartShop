package org.example.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartshop.enums.PaymentMethod;
import org.example.smartshop.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroPaiement;
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private PaymentMethod typePaiement;

    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statut;

    private String numeroRecu;
    private String numeroCheque;
    private String referenceVirement;
    private String banque;

    private LocalDate dateEcheance;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
