package org.example.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartshop.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerTier niveauFidelite = CustomerTier.BASIC;

    @Builder.Default
    private Integer totalOrders = 0;
    
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "client")
    private List<Order> orders;
}