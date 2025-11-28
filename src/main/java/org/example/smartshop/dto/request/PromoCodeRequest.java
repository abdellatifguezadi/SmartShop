package org.example.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeRequest {

    @NotBlank(message = "Le code promo est obligatoire")
    @Size(min = 3, max = 50, message = "Le code promo doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Le code promo doit contenir uniquement des lettres majuscules, chiffres et tirets")
    private String code;
}
