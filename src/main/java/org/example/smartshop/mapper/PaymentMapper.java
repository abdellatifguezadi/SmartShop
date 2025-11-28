package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.dto.response.PaymentResponse;
import org.example.smartshop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    Payment toEntity(PaymentRequest request);
    
    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);
}
