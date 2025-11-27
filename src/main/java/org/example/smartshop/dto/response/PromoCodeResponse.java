package org.example.smartshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeResponse {

    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private Boolean used;
}

