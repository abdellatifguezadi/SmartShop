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

    private BigDecimal discountPercentage;

    private Boolean used;

    public static BigDecimal getDiscountPercentage() {
        return new BigDecimal("0.05");
    }
}
