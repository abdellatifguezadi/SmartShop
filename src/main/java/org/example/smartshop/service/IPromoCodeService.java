package org.example.smartshop.service;

import org.example.smartshop.dto.request.PromoCodeRequest;
import org.example.smartshop.dto.response.PromoCodeResponse;

import java.util.List;

public interface IPromoCodeService {

    PromoCodeResponse createPromoCode(PromoCodeRequest request);

    List<PromoCodeResponse> getAllPromoCodes();

    void deletePromoCode(Long id);
}
