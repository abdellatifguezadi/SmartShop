package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.PromoCodeRequest;
import org.example.smartshop.dto.response.PromoCodeResponse;
import org.example.smartshop.service.IPromoCodeService;
import org.example.smartshop.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final IPromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<PromoCodeResponse> createPromoCode(@Valid @RequestBody PromoCodeRequest request , HttpSession session) {
        SecurityUtils.requireAdmin(session);
        PromoCodeResponse response = promoCodeService.createPromoCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PromoCodeResponse>> getAllPromoCodes(HttpSession session) {
        SecurityUtils.requireAdmin(session);
        List<PromoCodeResponse> responses = promoCodeService.getAllPromoCodes();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromoCode(@PathVariable Long id , HttpSession session) {
        SecurityUtils.requireAdmin(session);
        promoCodeService.deletePromoCode(id);
        return ResponseEntity.noContent().build();
    }
}
