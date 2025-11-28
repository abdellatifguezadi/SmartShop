package org.example.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "promo_codes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Builder.Default
    private BigDecimal discountPercentage = new BigDecimal("0.05");

    private Boolean used;

}
