package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.dto.response.PaymentResponse;
import org.example.smartshop.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    Payment toEntity(PaymentRequest request);
    
    PaymentResponse toResponse(Payment payment);
}
