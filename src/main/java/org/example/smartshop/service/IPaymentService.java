package org.example.smartshop.service;

import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.dto.response.PaymentResponse;

import java.util.List;

public interface IPaymentService {
    PaymentResponse createPayment(Long orderId, PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    List<PaymentResponse> getPaymentsByOrderId(Long orderId);
    PaymentResponse validatePayment(Long paymentId);
    PaymentResponse rejectedPayment(Long paymentId);
}
