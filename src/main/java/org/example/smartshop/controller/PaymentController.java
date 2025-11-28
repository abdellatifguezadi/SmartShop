package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.dto.response.PaymentResponse;
import org.example.smartshop.service.IPaymentService;
import org.example.smartshop.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request , HttpSession session ) {
        SecurityUtils.requireAdmin(session);
        PaymentResponse response = paymentService.createPayment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id,HttpSession session) {
        SecurityUtils.requireAdmin(session);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable Long orderId,HttpSession session) {
        SecurityUtils.requireAdmin(session);
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<PaymentResponse> validatePayment(@PathVariable Long id ,HttpSession session ){
        SecurityUtils.requireAdmin(session);
        PaymentResponse response = paymentService.validatePayment(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/rejected")
    public ResponseEntity<PaymentResponse> rejectedPayment(@PathVariable Long id , HttpSession session){
        SecurityUtils.requireAdmin(session);
        PaymentResponse response = paymentService.rejectedPayment(id);
        return ResponseEntity.ok(response);
    }
}
